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
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class ForgeItemRecipe extends AbstractForgeRecipe<ItemStack>
{
	final ItemStack result;

	public ForgeItemRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, ItemStack result)
	{
		super(catalyst.isPresent() ? catalyst : Optional.of(Ingredient.EMPTY), catalystName, essences, infusables, infusedProperties);
		this.result = result;

	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	protected ItemStack getResult() {
		return result;
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult()
	{
		return (input, registries, output) -> {

			ItemStack result = this.result.copy();

			if(ItemStack.isSameItem(result, input.getCurrentOutput()))
				result.setCount(result.getCount() + input.getCurrentOutput().getCount() - 1);

			return InfusedPropertiesHelper.addProperties(result, InfusedPropertiesHelper.getInfusedProperties(output));
		};
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_ITEM.get();
	}

}
