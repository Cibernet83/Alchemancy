package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class DurabilityMultiplierProperty extends Property
{
	private final int color;
	private final float multiplier;

	public DurabilityMultiplierProperty(int color, float multiplier) {
		this.color = color;
		this.multiplier = multiplier;
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.MAX_DAMAGE && data instanceof Integer)
			return (int) (((Integer)data) * multiplier);
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}
}
