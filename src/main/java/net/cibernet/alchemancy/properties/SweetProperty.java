package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class SweetProperty extends Property
{
	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.FOOD && data instanceof FoodProperties foodProperties)
			return new FoodProperties(foodProperties.nutrition(), foodProperties.saturation() * 1.5f, true, foodProperties.eatSeconds(),
					foodProperties.usingConvertsTo(), foodProperties.effects());
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFAC9FF;
	}
}
