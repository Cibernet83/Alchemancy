package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.cibernet.alchemancy.registries.AlchemancyTags;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ForgeChromaTintingRecipe extends AbstractForgeRecipe<Object> {
	public final Ingredient ingredient;

	protected ForgeChromaTintingRecipe(Ingredient ingredient) {
		super(Optional.empty(), Optional.empty(), List.of(), List.of());
		this.ingredient = ingredient;
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return !input.getCurrentOutput().is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS) && !ingredient.isEmpty() && input.testInfusables(List.of(ingredient), false);
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult() {
		return (grid, provider, stack) ->
		{
			ArrayList<Integer> colors = new ArrayList<>();

			for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(grid.getItemPedestals())) {

				ItemStack pedestalStack = pedestal.getItem();
				if (ingredient.test(pedestalStack))
				{
					var lensColors = Arrays.stream(AlchemancyProperties.TINTED.value().getData(pedestalStack)).toList();
					if(lensColors.isEmpty())
						colors.add(TintedProperty.DEFAULT_COLOR);
					else colors.addAll(lensColors);

					grid.markAsProcessed(pedestal);
				}
			}

			if (!colors.isEmpty()) {
				InfusedPropertiesHelper.addProperty(stack, AlchemancyProperties.TINTED);
				AlchemancyProperties.TINTED.value().setData(stack, colors.toArray(Integer[]::new));
			}

			return stack;
		};
	}

	@Override
	public int getPriority() {
		return 40;
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid) {
		return TriState.DEFAULT;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_CHROMA_TINTING.get();
	}

	public static class Serializer implements RecipeSerializer<ForgeChromaTintingRecipe> {

		private static final MapCodec<ForgeChromaTintingRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				Ingredient.CODEC.fieldOf("infusable").forGetter(recipe -> recipe.ingredient)
		).apply(instance, ForgeChromaTintingRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, ForgeChromaTintingRecipe> STREAM_CODEC = StreamCodec.composite(
				Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.ingredient, ForgeChromaTintingRecipe::new);

		@Override
		public MapCodec<ForgeChromaTintingRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ForgeChromaTintingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
