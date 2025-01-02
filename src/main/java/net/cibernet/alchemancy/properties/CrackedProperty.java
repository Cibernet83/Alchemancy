package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CrackedProperty extends Property
{
	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		return resultingAmount * (user.level().getRandom().nextFloat() < 0.4f ? 2 : 1);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x7A7A7A;
	}
}
