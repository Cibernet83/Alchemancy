package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

public class DormantPropertyInfusionRecipe extends AbstractForgeRecipe<Object>
{
	public DormantPropertyInfusionRecipe() {
		super(Optional.empty(), List.of(), List.of(), List.of());
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level)
	{
		return !input.getCurrentOutput().isEmpty() && !input.areIngredientsEmpty() && input.handleDormantRecipes(false);
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult() {
		return (grid, provider, currentItem) ->
		{
			grid.handleDormantRecipes(currentItem, true);
			return currentItem;
		};
	}

	@Override
	public boolean isTransmutation() {
		return false;
	}

	@Override
	protected Object getResult() {
		return null;
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.DORMANT_PROPERTIES.get();
	}

	public static class Serializer implements RecipeSerializer<DormantPropertyInfusionRecipe> {
		private final MapCodec<DormantPropertyInfusionRecipe> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, DormantPropertyInfusionRecipe> streamCodec;

		private final DormantPropertyInfusionRecipe INSTANCE = new DormantPropertyInfusionRecipe();

		public Serializer() {
			this.codec = RecordCodecBuilder.mapCodec(
					p_311736_ -> p_311736_.point(INSTANCE)
			);
			this.streamCodec = StreamCodec.unit(INSTANCE);
		}

		@Override
		public MapCodec<DormantPropertyInfusionRecipe> codec() {
			return this.codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DormantPropertyInfusionRecipe> streamCodec() {
			return this.streamCodec;
		}

		@FunctionalInterface
		public interface Factory {
			DormantPropertyInfusionRecipe create();
		}
	}

}
