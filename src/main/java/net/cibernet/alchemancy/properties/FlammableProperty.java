package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FlammableProperty extends Property
{
	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(user != null && user.isOnFire())
			target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), (int) (user.getRemainingFireTicks() * 1.5f)));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(user.isOnFire())
		{
			if(user.getRemainingFireTicks() % 20 == 0)
				user.setRemainingFireTicks(user.getRemainingFireTicks() + 10);
			if(user.getRandom().nextFloat() < 0.01f && InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.FLAMMABLE))
			{
				InfusedPropertiesHelper.removeProperty(stack, AlchemancyProperties.FLAMMABLE);
				InfusedPropertiesHelper.addProperty(stack, AlchemancyProperties.CHARRED);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xB8945F;
	}
}
