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

public class ForgeRemovePropertiesRecipe extends AbstractForgeRecipe<Optional<ItemStack>>
{
	final Optional<ItemStack> result;

	public ForgeRemovePropertiesRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, Optional<ItemStack> result)
	{
		super(catalyst.isPresent() ? catalyst : Optional.of(Ingredient.EMPTY), catalystName, essences, infusables, infusedProperties);
		this.result = result;

	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level)
	{
		return  (catalyst.isEmpty() || catalyst.get().isEmpty() || catalyst.get().test(input.getCurrentOutput())) &&
				(catalystName.isEmpty() || input.getCurrentOutput().getDisplayName().getString().equalsIgnoreCase(catalystName.get())) &&
				input.testInfusables(infusables, false) &&
				input.testEssences(essences, false) &&
				input.testProperties(infusedProperties, false);
	}
	@Override
	public int getPriority() {
		return 60;
	}

	@Override
	protected Optional<ItemStack> getResult() {
		return result;
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult()
	{
		return (input, registries, output) -> {

			ItemStack result = this.result.map(ItemStack::copy).orElseGet(input::getCurrentOutput);

			if(ItemStack.isSameItem(result, input.getCurrentOutput()))
				result.setCount(result.getCount() + input.getCurrentOutput().getCount() - 1);

			return InfusedPropertiesHelper.clearAllInfusions(result);
		};
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result.orElse(ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.REMOVE_PROPERTIES.get();
	}


}
