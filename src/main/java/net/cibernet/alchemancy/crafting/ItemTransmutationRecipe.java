package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ItemTransmutationRecipe extends ForgeItemRecipe
{
	public ItemTransmutationRecipe(Optional<Ingredient> catalyst, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, ItemStack result) {
		super(catalyst, List.of(), List.of(), List.of(), result);
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return input.canPerformTransmutation() && (catalyst.isEmpty() || catalyst.get().test(input.getCurrentOutput()));
	}

	@Override
	public boolean isTransmutation() {
		return true;
	}

	@Override
	public int getPriority() {
		return 100;
	}
}
