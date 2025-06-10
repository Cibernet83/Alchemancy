package net.cibernet.alchemancy.events.handler.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class RenderEventHandler
{
	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event)
	{
		Player player = Minecraft.getInstance().player;
		if(player != null && !player.getUseItem().canPerformAction(ItemAbilities.SHIELD_BLOCK) &&
				InfusedPropertiesHelper.hasProperty(player.getUseItem(), AlchemancyProperties.SHIELDING) && player.getUsedItemHand() == event.getHand())
		{

			PoseStack poseStack = event.getPoseStack();

			int sign = event.getHand() == (player.getMainArm() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND) ? 1 : -1;

			poseStack.translate(-sign * 0.25f, 0.4f, -0.25f);
			poseStack.mulPose(Axis.YP.rotationDegrees(sign * 75.0F));
			poseStack.mulPose(Axis.XP.rotationDegrees(270));

		}
	}

	@SubscribeEvent
	public static void modifyFogColor(ViewportEvent.ComputeFogColor event) {
		@Nullable Vector3f tint = getScreenTintColor();
		if(tint == null) return;

		event.setRed(event.getRed() * tint.x());
		event.setGreen(event.getGreen() * tint.y());
		event.setBlue(event.getBlue() * tint.z());
	}

	public static boolean modifySkyColor(float red, float green, float blue, float alpha, Operation<Void> original) {
		@Nullable Vector3f tint = getScreenTintColor();
		if(tint != null)
		{
			original.call(red * tint.x(), green * tint.y(), blue * tint.z(), alpha);
			return true;
		} return false;
	}

	@SubscribeEvent
	public static void onStageRender(RenderLevelStageEvent event) {

		@Nullable Vector3f tint = getScreenTintColor();
		if(tint == null) return;

		var shaderColor = RenderSystem.getShaderColor();
		RenderSystem.setShaderColor(tint.x(), tint.y(), tint.z(), shaderColor[3]);
	}

	@SubscribeEvent
	public static void onStageRender(RenderFrameEvent.Pre event) {

		@Nullable Vector3f tint = getScreenTintColor();
		if(tint == null) return;

		var shaderColor = RenderSystem.getShaderColor();
		//RenderSystem.setShaderColor(shaderColor[0] * tint.x(), shaderColor[1] * tint.y(), shaderColor[2] * tint.z(), shaderColor[3]);
	}

	@Nullable
	private static Vector3f getScreenTintColor() {
		Player player = Minecraft.getInstance().player;

		if(player == null)
			return null;
		var stack = player.getItemBySlot(EquipmentSlot.HEAD);
		if(!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.TINTED_LENS))
			return null;

		return Vec3.fromRGB24(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.TINTED) ?
				AlchemancyProperties.TINTED.get().getColor(stack) :
				AlchemancyProperties.TINTED_LENS.get().getColor(stack)).toVector3f();
	}

}
