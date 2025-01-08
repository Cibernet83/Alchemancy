package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class RestoreClayMoldSmeltingRecipe extends SmeltingRecipe
{
	public static final ItemStack DEFAULT_RESULT = Items.CLAY_BALL.getDefaultInstance();

	public RestoreClayMoldSmeltingRecipe(String group, float experience, int cookingTime) {
		super(group, CookingBookCategory.MISC, Ingredient.of(AlchemancyItems.UNSHAPED_CLAY), DEFAULT_RESULT, experience, cookingTime);
		InfusedPropertiesHelper.addProperty(DEFAULT_RESULT, AlchemancyProperties.HARDENED);
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level)
	{
		return InfusedPropertiesHelper.hasProperty(input.item(), AlchemancyProperties.CLAY_MOLD);
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries)
	{
		ItemStack storedItem = AlchemancyProperties.CLAY_MOLD.get().getData(input.item());
		if(storedItem.isDamageableItem())
			storedItem.setDamageValue(0);
		InfusedPropertiesHelper.removeProperty(storedItem, AlchemancyProperties.MALLEABLE);
		InfusedPropertiesHelper.addProperty(storedItem, AlchemancyProperties.HARDENED);

		return storedItem;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlchemancyRecipeTypes.Serializers.RESTORE_CLAY_MOLD_SMELTING.get();
	}

	public static class Serializer implements RecipeSerializer<RestoreClayMoldSmeltingRecipe>
	{
		private static final MapCodec<RestoreClayMoldSmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(
				p_300831_ -> p_300831_.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(RestoreClayMoldSmeltingRecipe::getGroup),
								Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(RestoreClayMoldSmeltingRecipe::getExperience),
								Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(RestoreClayMoldSmeltingRecipe::getCookingTime)
						)
						.apply(p_300831_, RestoreClayMoldSmeltingRecipe::new)
		);

		private static final StreamCodec<RegistryFriendlyByteBuf, RestoreClayMoldSmeltingRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, AbstractCookingRecipe::getGroup,
				ByteBufCodecs.FLOAT, RestoreClayMoldSmeltingRecipe::getExperience,
				ByteBufCodecs.INT, RestoreClayMoldSmeltingRecipe::getCookingTime,
				RestoreClayMoldSmeltingRecipe::new
				);

		@Override
		public MapCodec<RestoreClayMoldSmeltingRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, RestoreClayMoldSmeltingRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
