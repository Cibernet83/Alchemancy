package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class UndeadProperty extends Property
{
	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		return stack.getDamageValue() <= resultingAmount ? stack.getMaxDamage() : -resultingAmount;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x4E7B36;
	}

	@Override
	public int getPriority() {
		return Priority.LOWEST;
	}
}
