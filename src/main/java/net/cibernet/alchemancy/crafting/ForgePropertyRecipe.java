package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class ForgePropertyRecipe extends AbstractForgeRecipe<Holder<Property>>
{
	final Holder<Property> result;

	public ForgePropertyRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, Holder<Property> result)
	{
		super(catalyst, catalystName, essences, infusables, infusedProperties);
		this.result = result;
	}


	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return !InfusedPropertiesHelper.hasProperty(input.getCurrentOutput(), result) && super.matches(input, level);
	}

	@Override
	public Holder<Property> getResult() {
		return result;
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult() {
		return (input, registries, resultItem) -> InfusedPropertiesHelper.addProperty(resultItem, result);
	}


	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_PROPERTY.get();
	}

}
