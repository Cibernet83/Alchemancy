package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.blocks.blockentities.EssenceExtractorBlockEntity;
import net.cibernet.alchemancy.essence.Essence;
import net.cibernet.alchemancy.registries.AlchemancyEssence;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class EssenceExtractionRecipe implements Recipe<EssenceExtractorBlockEntity>
{
	final Ingredient ingredient;
	final EssenceContainer essence = new EssenceContainer(1000);

	public EssenceExtractionRecipe(Ingredient ingredient, Holder<Essence> essence, int amount)
	{
		this.ingredient = ingredient;
		this.essence.replace(essence.value(), amount);
	}

	@Override
	public boolean matches(EssenceExtractorBlockEntity input, Level level)
	{
		return ingredient.test(input.getItem(0)) && input.storedEssence.canAdd(essence, true);
	}

	@Override
	public ItemStack assemble(EssenceExtractorBlockEntity input, HolderLookup.Provider registries)
	{
		input.removeItem(0, 1);
		input.storedEssence.add(essence.getEssence(), essence.getAmount());
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.ESSENCE_EXTRACTION.get();
	}

	@Override
	public RecipeType<?> getType() {
		return AlchemancyRecipeTypes.ESSENCE_EXTRACTION.get();
	}

	public static class Serializer implements RecipeSerializer<EssenceExtractionRecipe>
	{
		private static final MapCodec<EssenceExtractionRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) ->  instance.group(
				Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
				Essence.CODEC.fieldOf("essence").forGetter(recipe -> Holder.direct(recipe.essence.getEssence())),
				Codec.INT.fieldOf("amount").forGetter(recipe -> recipe.essence.getAmount())
			).apply(instance, EssenceExtractionRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, EssenceExtractionRecipe> STREAM_CODEC = StreamCodec.of(
				(encode, recipe) ->
				{

					Ingredient.CONTENTS_STREAM_CODEC.encode(encode, recipe.ingredient);
					ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey()).encode(encode, encode.registryAccess().holder(ResourceKey.create(AlchemancyEssence.REGISTRY.getRegistryKey(), recipe.essence.getEssence().getKey())).get());
					encode.writeInt(recipe.essence.getAmount());
				},
				decode -> new EssenceExtractionRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(decode), ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey()).decode(decode), decode.readInt())
		);

		@Override
		public MapCodec<EssenceExtractionRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, EssenceExtractionRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
