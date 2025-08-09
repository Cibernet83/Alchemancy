package net.cibernet.alchemancy.mixin;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.AuxiliaryProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin
{
	@Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot1);

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", ordinal = 1, shift = At.Shift.BEFORE))
	public void tickAfterSpectatorCheck(CallbackInfo ci)
	{
		CommonUtils.tickInventoryItemProperties(((Player)(Object) this));
	}

	@Inject(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("HEAD"), cancellable = true)
	public void canFit(Pose pose, CallbackInfoReturnable<Boolean> cir) {
		if(InfusedPropertiesHelper.hasItemWithProperty((Player)(Object) this, AlchemancyProperties.PHASE_STEP, true))
			cir.setReturnValue(true);
	}

	@Inject(method = "canUseSlot", at = @At("HEAD"), cancellable = true)
	public void canUseSlot(EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
		if(InfusedPropertiesHelper.hasProperty(getItemBySlot(slot), AlchemancyProperties.UNMOVABLE))
			cir.setReturnValue(false);
	}
}
