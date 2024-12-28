package net.cibernet.alchemancy.mixin.client;

import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IClientItemExtensions.class)
public interface ClientItemExtensionsMixin
{
	@Inject(method = "getArmorLayerTintColor", at = @At("RETURN"), cancellable = true)
	default void getArmorLayerTintColor(ItemStack stack, LivingEntity entity, ArmorMaterial.Layer layer, int layerIdx, int fallbackColor, CallbackInfoReturnable<Integer> cir)
	{
		cir.setReturnValue(CommonUtils.getPropertyDrivenTint(stack, layerIdx, layer.dyeable() ? fallbackColor : 0xFFFFFFFF));
	}

}
