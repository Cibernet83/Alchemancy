package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin
{
	@Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
	public void drop(boolean dropStack, CallbackInfoReturnable<Boolean> cir)
	{
		ItemStack selectedItem = ((LocalPlayer)(Object)this).getInventory().getSelected();
		if(InfusedPropertiesHelper.hasInfusedProperty(selectedItem, AlchemancyProperties.STICKY) ||
				InfusedPropertiesHelper.hasInfusedProperty(selectedItem, AlchemancyProperties.UNMOVABLE))
			cir.setReturnValue(false);
	}

	@Inject(method = "playSound", at = @At("HEAD"))
	public void playSound(SoundEvent sound, float volume, float pitch, CallbackInfo ci, @Local(ordinal = 0, argsOnly = true) LocalFloatRef volumeRef)
	{
		LocalPlayer self = (LocalPlayer) (Object) this;
		float muffleMod = 1;
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if(slot.isArmor() && InfusedPropertiesHelper.hasInfusedProperty(self.getItemBySlot(slot), AlchemancyProperties.MUFFLED))
				muffleMod -= 0.25f;
		}
		volumeRef.set(volume * muffleMod);
	}

	@WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
	public boolean modifyMovementInput(LocalPlayer instance, Operation<Boolean> original)
	{
		return original.call(instance) && !InfusedPropertiesHelper.hasItemWithProperty(instance, AlchemancyProperties.DEXTEROUS, true);
	}
}
