package net.cibernet.alchemancy.properties;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class UndeadProperty extends Property
{
	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
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
