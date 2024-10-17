package net.cibernet.alchemancy.mixin.client;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
	@Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
	public void drop(boolean dropStack, CallbackInfoReturnable<Boolean> cir)
	{
		ItemStack selectedItem = ((LocalPlayer)(Object)this).getInventory().getSelected();
		if(InfusedPropertiesHelper.hasInfusedProperty(selectedItem, AlchemancyProperties.STICKY) ||
				InfusedPropertiesHelper.hasInfusedProperty(selectedItem, AlchemancyProperties.QUANTUM_BIND))
			cir.setReturnValue(false);
	}
}
