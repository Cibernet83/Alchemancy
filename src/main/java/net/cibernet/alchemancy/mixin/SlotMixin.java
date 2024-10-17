package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin
{
	@Shadow public abstract ItemStack getItem();

	@Inject(method = "mayPickup", cancellable = true, at = @At("RETURN"))
	public void mayPickUp(Player player, CallbackInfoReturnable<Boolean> cir)
	{
		if(cir.getReturnValue() && InfusedPropertiesHelper.hasProperty(getItem(), AlchemancyProperties.QUANTUM_BIND))
			cir.setReturnValue(false);
	}
}
