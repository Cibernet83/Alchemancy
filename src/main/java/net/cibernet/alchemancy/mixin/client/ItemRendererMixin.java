package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
	@Shadow public abstract BakedModel getModel(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int seed);

	@Unique
	private LivingEntity alchemancy$livingEntity = null;

	@Inject(method = "renderQuadList", at = @At(value = "JUMP", shift = At.Shift.AFTER))
	public void modifyColorNoTint(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, ItemStack itemStack, int combinedLight, int combinedOverlay, CallbackInfo ci,
	                            @Local(ordinal = 2) LocalIntRef localTint, @Local BakedQuad quad)
	{
		if(!quad.isTinted())
			CommonUtils.modifyTint(itemStack, quad.getTintIndex(), localTint);
	}

	@Inject(method = "renderQuadList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/color/item/ItemColors;getColor(Lnet/minecraft/world/item/ItemStack;I)I", shift = At.Shift.AFTER))
	public void modifyColorWithTint(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, ItemStack itemStack, int combinedLight, int combinedOverlay, CallbackInfo ci,
	                                @Local(ordinal = 2) LocalIntRef localTint, @Local BakedQuad quad)
	{
		if(quad.isTinted())
			CommonUtils.modifyTint(itemStack, quad.getTintIndex(), localTint);
	}


	@Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
	public void renderStatic(LivingEntity entity, ItemStack itemStack, ItemDisplayContext diplayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, Level level, int combinedLight, int combinedOverlay, int seed, CallbackInfo ci)
	{
		this.alchemancy$livingEntity = entity;
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci,
	                  @Local(argsOnly = true) LocalRef<BakedModel> localModel, @Local(ordinal = 0, argsOnly = true)LocalIntRef localLight, @Local(ordinal = 0, argsOnly = true) LocalRef<ItemStack> localStack)
	{
		if(displayContext != ItemDisplayContext.GUI && InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.CONCEALED))
			ci.cancel();
		else
		{
			if(InfusedPropertiesHelper.hasInfusedProperty(itemStack, AlchemancyProperties.DISGUISED))
			{
				ItemStack disguise = AlchemancyProperties.DISGUISED.get().getData(itemStack);

				LivingEntity owner = displayContext == ItemDisplayContext.GUI ? Minecraft.getInstance().player : alchemancy$livingEntity;

				if(!disguise.isEmpty())
				{
					localStack.set(disguise);
					localModel.set(getModel(disguise, owner == null ? Minecraft.getInstance().level : owner.level(), owner, owner == null ? 0 : owner.getId()));
				}
				alchemancy$livingEntity = null;
			}
			if(InfusedPropertiesHelper.hasInfusedProperty(itemStack, AlchemancyProperties.GLOWING_AURA))
				localLight.set(LightTexture.FULL_BRIGHT);
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;handleCameraTransforms(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemDisplayContext;Z)Lnet/minecraft/client/resources/model/BakedModel;"))
	public void renderScale(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci)
	{
		if(InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.RESIZED))
		{
			float size = AlchemancyProperties.RESIZED.get().getData(itemStack);
			poseStack.scale(size, size, size);
		}
	}

}
