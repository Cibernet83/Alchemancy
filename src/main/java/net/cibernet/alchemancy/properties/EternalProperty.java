package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class EternalProperty extends Property
{
	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		if(!entity.level().isClientSide() && entity.getAge() > -32768)
			entity.setUnlimitedLifetime();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF2106;
	}
}
