package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(Gui.class)
public class GuiMixin {

	@WrapOperation(method = "renderCameraOverlays", at = @At(value = "INVOKE", ordinal = 0,
			target = "Lnet/minecraft/client/gui/Gui;renderTextureOverlay(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;F)V"))
	public void cancelPumpkinOverlay(Gui instance, GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha, Operation<Void> original, @Local(ordinal = 0) ItemStack itemStack) {

		if(!InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.SEETHROUGH))
			original.call(instance, guiGraphics, shaderLocation, alpha);
	}
	@WrapOperation(method = "renderCameraOverlays", at = @At(value = "INVOKE",
			target = "Lnet/neoforged/neoforge/client/extensions/common/IClientItemExtensions;renderHelmetOverlay(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
	public void cancelHelmetOverlay(IClientItemExtensions instance, ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original, @Local(ordinal = 0) ItemStack itemStack) {

		if(!InfusedPropertiesHelper.hasProperty(itemStack, AlchemancyProperties.SEETHROUGH))
			original.call(instance, stack, player, guiGraphics, deltaTracker);
	}
}
