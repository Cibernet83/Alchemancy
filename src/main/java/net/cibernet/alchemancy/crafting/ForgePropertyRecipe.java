package net.cibernet.alchemancy.crafting;

import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class ForgePropertyRecipe extends AbstractForgeRecipe<List<Holder<Property>>>
{
	public static final StreamCodec<RegistryFriendlyByteBuf, List<Holder<Property>>> PROPERTY_LIST_STREAM_CODEC = StreamCodec.of(
			(encode, list) ->
			{
				encode.writeInt(list.size());
				for (Holder<Property> propertyHolder : list) {
					Property.STREAM_CODEC.encode(encode, propertyHolder);
				}
			},
			(decode) ->
			{
				int listSize = decode.readInt();
				List<Holder<Property>> result = new ArrayList<>();
				for(int i = 0; i < listSize; i++)
					result.add(Property.STREAM_CODEC.decode(decode));
				return result;
			}
	);
	final List<Holder<Property>> result;


	public ForgePropertyRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties, List<Holder<Property>> result)
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
		return checkParadoxical(input.getCurrentOutput()) && super.matches(input, level);
	}

	@Override
	public List<Holder<Property>> getResult() {
		return result;
	}

	@Override
	public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid)
	{
		if(forgeRecipePredicate.outputProperties().isEmpty() || forgeRecipePredicate.outputProperties().get().isEmpty())
			return TriState.DEFAULT;
		return new HashSet<>(result).containsAll(forgeRecipePredicate.outputProperties().get()) ? TriState.TRUE : TriState.FALSE;
	}


	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult()
	{
		return (input, registries, resultItem) ->
		{
			for (Holder<Property> propertyHolder : result) {
				InfusedPropertiesHelper.addProperty(resultItem, propertyHolder);
			}
			return resultItem;
		};
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return InfusedPropertiesHelper.createPropertyIngredient(result);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_PROPERTY.get();
	}

}
