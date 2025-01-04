package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class CompactProperty extends Property
{
	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.MAX_STACK_SIZE && !stack.is(AlchemancyTags.Items.DISABLES_COMPACT) && data instanceof Integer maxStackSize)
		{
			for (DataComponentType<?> dataComponentType : stack.getComponents().keySet()) {
				if(BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(dataComponentType).is(AlchemancyTags.DataComponents.DISABLES_COMPACT))
					return data;
			}
			return Math.min(96, maxStackSize * 4);
		}

		return data;
	}

	@Override
	public int getPriority() {
		return Priority.HIGHEST;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xB5E0FF;
	}
}
