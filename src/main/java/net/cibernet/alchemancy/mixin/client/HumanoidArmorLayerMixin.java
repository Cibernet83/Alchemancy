package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>>
{

	@Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
	public void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, CallbackInfo ci, @Local(argsOnly = true, ordinal = 0)LocalIntRef lightRef)
	{
		ItemStack stack = livingEntity.getItemBySlot(slot);
		if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CONCEALED))
		{
			ci.cancel();
			return;
		}

		poseStack.pushPose();
		float scale = AlchemancyProperties.RESIZED.value().getData(stack);
		if(scale != 1)
			poseStack.scale(scale, scale, scale);


		if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GLOWING_AURA))
			lightRef.set(LightTexture.FULL_BRIGHT);
	}

//	@Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
//			at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;getArmorTexture(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ArmorMaterial$Layer;ZLnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/resources/ResourceLocation;",
//			shift = At.Shift.BEFORE), cancellable = true)
//	public void modifyArmorTint(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, @Local(ordinal = 2) LocalIntRef localInt)
//	{
//		ItemStack stack = livingEntity.getItemBySlot(slot);
//		CommonUtils.modifyTint(stack, -1, localInt);
//	}



	@WrapOperation(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
	public ItemStack modifyArmorItem(LivingEntity instance, EquipmentSlot equipmentSlot, Operation<ItemStack> original)
	{
		ItemStack stack = original.call(instance, equipmentSlot);
		ItemStack disguise = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DISGUISED) ? AlchemancyProperties.DISGUISED.get().getData(stack) : ItemStack.EMPTY;

		return disguise.isEmpty() ? stack : disguise;
	}

	@Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V", at = @At("RETURN"))
	public void renderArmorPieceTail(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, CallbackInfo ci)
	{
		poseStack.popPose();
	}


}
