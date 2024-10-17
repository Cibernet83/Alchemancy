package net.cibernet.alchemancy.events.handler.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.ItemAbilities;

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
}
