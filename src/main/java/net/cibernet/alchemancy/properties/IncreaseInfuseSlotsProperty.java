package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class IncreaseInfuseSlotsProperty extends Property
{
	public final int slots;

	protected IncreaseInfuseSlotsProperty(int slots) {
		this.slots = slots;
	}

	public static Property simple(int slots, UnaryOperator<Style> style, Supplier<Integer> colorSupplier)
	{
		return new IncreaseInfuseSlotsProperty(slots) {
			@Override
			public int getColor(ItemStack stack) {
				return colorSupplier.get();
			}

			@Override
			public Component getDisplayText(ItemStack stack) {
				return super.getDisplayText(stack).copy().withStyle(style);
			}
		};
	}

	public static Property simple(int slots, int color)
	{
		return simple(slots, (style) -> style, () -> color);
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return (dataType == AlchemancyItems.Components.INFUSION_SLOTS.get() && data instanceof Integer dataInt) ?
				dataInt + slots : super.modifyDataComponent(stack, dataType, data);
	}
}
