package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

import static net.cibernet.alchemancy.registries.AlchemancyRecipeTypes.Serializers.ITEM_TRANSMUTATION;

public class ItemTransmutationRecipe extends ForgeItemRecipe
{
	public ItemTransmutationRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, ItemStack result) {
		super(catalyst, catalystName, List.of(), List.of(), List.of(), result);
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return input.canPerformTransmutation() && (catalyst.isEmpty() || catalyst.get().test(input.getCurrentOutput()));
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ITEM_TRANSMUTATION.get();
	}

	@Override
	public boolean isTransmutation() {
		return true;
	}

	@Override
	public int getPriority() {
		return 100;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ITEM_TRANSMUTATION.get();
	}
}
