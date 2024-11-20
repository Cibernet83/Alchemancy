package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin
{
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"),
			method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
	public void renderToBuffer(ElytraModel instance, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, Operation<Void> original,
	                           @Local(ordinal = 0) ItemStack itemStack)
	{
		if(InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.CONCEALED))
			return;
		if(InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.GLOWING_AURA))
			packedLight = LightTexture.FULL_BRIGHT;

		float scale = AlchemancyProperties.RESIZED.value().getData(itemStack);
		if(scale != 1)
			poseStack.scale(scale, scale, scale);

		int tint = CommonUtils.getPropertyDrivenTint(itemStack, -1, -1);
		if (tint == -1)
			original.call(instance, poseStack, vertexConsumer, packedLight, packedOverlay);
		else instance.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, tint);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
			method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V")
	public RenderType getRenderType(ResourceLocation location, Operation<RenderType> original, @Local ItemStack itemStack)
	{
		if(CommonUtils.hasPropertyDrivenAlpha(itemStack))
			return RenderType.entityTranslucent(location, false);
		return original.call(location);
	}
}
