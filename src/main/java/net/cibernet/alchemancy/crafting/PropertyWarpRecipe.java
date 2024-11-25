package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class PropertyWarpRecipe extends AbstractForgeRecipe<List<Holder<Property>>>
{
	final List<Holder<Property>> result;

	public PropertyWarpRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, List<Holder<Property>> result) {
		super(catalyst, catalystName, List.of(), List.of(), infusedProperties);
		this.result = result;
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return !input.hasBeenWarped(infusedProperties) && InfusedPropertiesHelper.hasProperty(input.getCurrentOutput(), AlchemancyProperties.WARPED) && super.matches(input, level);
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult()
	{
		return (input, registries, resultItem) ->
		{
			for (Holder<Property> propertyHolder : result) {
				InfusedPropertiesHelper.addProperty(resultItem, propertyHolder);
			}
			input.consumeWarped(result);
			return resultItem;
		};
	}

	@Override
	public int getPriority() {
		return -1;
	}

	@Override
	public List<Holder<Property>> getResult()
	{
		return result;
	}

	@Override
	public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid)
	{
		if(forgeRecipePredicate.outputProperties().isEmpty() || forgeRecipePredicate.outputProperties().get().isEmpty())
			return TriState.DEFAULT;
		return new HashSet<>(forgeRecipePredicate.outputProperties().get()).containsAll(result) ? TriState.TRUE : TriState.FALSE;
	}


	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.PROPERTY_WARP.get();
	}
}
