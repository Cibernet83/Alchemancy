package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class IncreaseInfuseSlotsProperty extends Property
{
	public final int slots;

	protected IncreaseInfuseSlotsProperty(int slots) {
		this.slots = slots;
	}

	public static Property simple(int slots, UnaryOperator<Style> style, Function<ItemStack, Integer> colorSupplier, BiFunction<DeferredItem<Item>, Holder<Property>, Collection<ItemStack>> populateCreativeTab)
	{
		return new IncreaseInfuseSlotsProperty(slots) {
			@Override
			public int getColor(ItemStack stack) {
				return colorSupplier.apply(stack);
			}

			@Override
			public Component getDisplayText(ItemStack stack) {
				return super.getDisplayText(stack).copy().withStyle(style);
			}

			@Override
			public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
				return populateCreativeTab.apply(capsuleItem, holder);
			}
		};
	}

	public static Property simple(int slots, int color)
	{
		return simple(slots, (style) -> style, (stack) -> color, (capsuleItem, holder) -> List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), holder)));
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return (dataType == AlchemancyItems.Components.INFUSION_SLOTS.get() && data instanceof Integer dataInt) ?
				dataInt + slots + PropertyModifierComponent.get(stack, asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS) : super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		int bonusSlots = PropertyModifierComponent.get(stack, asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS);
		return bonusSlots > 0 ? Component.translatable("property.detail.item_count", super.getDisplayText(stack), bonusSlots + 1).setStyle(Style.EMPTY.withColor(getColor(stack))) : super.getName(stack);
	}

	@Override
	public Component getName(ItemStack stack)
	{
		return getDisplayText(stack);
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd)
	{
		if(super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd))
		{
			PropertyModifierComponent.set(stack, asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS, PropertyModifierComponent.get(propertySource, asHolder(), AlchemancyProperties.Modifiers.BONUS_SLOTS));
			return true;
		}
		else return false;
	}

	private static final int[] LIMIT_BREAK_BASE = new int[] {0xFF9D14, 0xFFE14F, 0xFFFF9B, 0xFFFFFF};
	private static final int[] LIMIT_BREAK_ENHANCED = new int[] {0x00FFFF, 0x49FFFF, 0x93FFFF, 0xD8FFFF};
	private static final int[] LIMIT_BREAK_CREATIVE = new int[] {0xCA51FF, 0xF84FFF, 0xFF9BF2, 0xFFD8FA};

	public static int limitBreakColors(ItemStack stack)
	{
		int bonusSlots = PropertyModifierComponent.get(stack, AlchemancyProperties.LIMIT_BREAK, AlchemancyProperties.Modifiers.BONUS_SLOTS);
		return ColorUtils.interpolateColorsOverTime(0.15f, bonusSlots >= 99 ? LIMIT_BREAK_CREATIVE : bonusSlots >= 9 ? LIMIT_BREAK_ENHANCED : LIMIT_BREAK_BASE);
	}

	public static Collection<ItemStack> limitBreakCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> propertyHolder)
	{
		ItemStack creativeLimitBreak = InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), propertyHolder);
		PropertyModifierComponent.set(creativeLimitBreak, propertyHolder, AlchemancyProperties.Modifiers.BONUS_SLOTS, 999);
		return List.of(InfusedPropertiesHelper.storeProperties(capsuleItem.toStack(), propertyHolder), creativeLimitBreak);
	}
}
