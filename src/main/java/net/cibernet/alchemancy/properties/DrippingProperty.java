package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class DrippingProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xA08D71;
	}

	@Override
	public void onAttack(@org.jetbrains.annotations.Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		drip(target.level(), user, target, weapon);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		drip(user.level(), user, user, weapon);
	}

	public boolean drip(@Nullable Level level, Entity user, @Nullable Entity target, ItemStack stack)
	{
		if(target == null && user == null)
			return false;

		if(target == null)
			target = user;

		return AlchemancyProperties.BUCKETING.get().placeLiquid(level, target.blockPosition(), stack, target instanceof Player player ? player : null, null) ||
				(user != null && AlchemancyProperties.HOLLOW.get().dropItems(stack, user)) ||
				AlchemancyProperties.CAPTURING.get().releaseMob(level, stack, target.position(), null);
	}
}
