package net.cibernet.alchemancy.crafting.ingredient;

import net.cibernet.alchemancy.registries.AlchemancyIngredients;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

public class WaterBottleIngredient implements ICustomIngredient
{
	@Override
	public boolean test(ItemStack stack)
	{
		return stack.is(Items.POTION) && stack.has(DataComponents.POTION_CONTENTS) && stack.get(DataComponents.POTION_CONTENTS).is(Potions.WATER);
	}

	private final Stream<ItemStack> ITEM_STREAM = Stream.of(PotionContents.createItemStack(Items.POTION, Potions.WATER));

	@Override
	public Stream<ItemStack> getItems()
	{

		return ITEM_STREAM;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return AlchemancyIngredients.WATER_BOTTLE.get();
	}
}
