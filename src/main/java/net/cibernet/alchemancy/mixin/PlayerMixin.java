package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin
{
	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z"))
	public boolean setNoPhysics(Player instance, Operation<Boolean> original)
	{
		for (EquipmentSlot slot : EquipmentSlot.values())
		{
			if(slot.isArmor() && InfusedPropertiesHelper.hasProperty(instance.getItemBySlot(slot), AlchemancyProperties.PHASING))
				return true;
		}
		return original.call(instance);
	}
}
