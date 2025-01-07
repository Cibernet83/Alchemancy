package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class RestoreClayMoldCraftingRecipe extends CustomRecipe
{
	public static final SimpleCraftingRecipeSerializer<RestoreClayMoldCraftingRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(RestoreClayMoldCraftingRecipe::new);

	public RestoreClayMoldCraftingRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		if(input.ingredientCount() != 2)
			return false;

		boolean hasClay = false;
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack stack = input.getItem(i);

			if(stack.isEmpty())
				continue;

			if(!hasClay && stack.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY))
				hasClay = true;
			else if(!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLAY_MOLD))
				return false;
		}

		return hasClay;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);

			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLAY_MOLD))
				return ClayMoldProperty.repair(AlchemancyProperties.CLAY_MOLD.get().getData(stack));
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
