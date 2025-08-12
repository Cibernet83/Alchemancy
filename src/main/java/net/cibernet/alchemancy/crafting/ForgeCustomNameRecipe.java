package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.TintedProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ForgeCustomNameRecipe extends AbstractForgeRecipe<Object>
{
	public final Ingredient ingredient;
	public final Optional<Holder<Property>> bold;
	public final Optional<Holder<Property>> italic;
	public final Optional<Holder<Property>> strikethrough;
	public final Optional<Holder<Property>> underlined;
	public final Optional<Holder<Property>> obfuscated;
	public final boolean canTint;

	protected ForgeCustomNameRecipe(Ingredient ingredient, Optional<Holder<Property>> bold, Optional<Holder<Property>> italic, Optional<Holder<Property>> strikethrough, Optional<Holder<Property>> underlined, Optional<Holder<Property>> obfuscated, Optional<Boolean> canTint)
	{
		super(Optional.empty(), Optional.empty(), List.of(), List.of());
		this.ingredient = ingredient;
		this.bold = bold;
		this.italic = italic;
		this.strikethrough = strikethrough;
		this.underlined = underlined;
		this.obfuscated = obfuscated;
		this.canTint = canTint.orElse(true);
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level) {
		return !ingredient.isEmpty() && input.testInfusables(List.of(ingredient), false);
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult() {
		return (grid, provider, stack) ->
		{
			MutableComponent name = Component.empty();
			boolean setName = false;
			for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(grid.getItemPedestals())) {

				ItemStack pedestalStack = pedestal.getItem();
				if(ingredient.test(pedestalStack))
				{
					MutableComponent pedestalName = pedestalStack.getHoverName().copy();

					pedestalName.withStyle(style ->
					{
						AtomicReference<Style> result = new AtomicReference<>(style);
						bold.ifPresent(propertyHolder -> result.set(result.get().withBold(InfusedPropertiesHelper.hasProperty(pedestalStack, propertyHolder))));
						italic.ifPresent(propertyHolder -> result.set(result.get().withItalic(InfusedPropertiesHelper.hasProperty(pedestalStack, propertyHolder))));
						strikethrough.ifPresent(propertyHolder -> result.set(result.get().withStrikethrough(InfusedPropertiesHelper.hasProperty(pedestalStack, propertyHolder))));
						underlined.ifPresent(propertyHolder -> result.set(result.get().withUnderlined(InfusedPropertiesHelper.hasProperty(pedestalStack, propertyHolder))));
						obfuscated.ifPresent(propertyHolder -> result.set(result.get().withObfuscated(InfusedPropertiesHelper.hasProperty(pedestalStack, propertyHolder))));

						if(canTint && InfusedPropertiesHelper.hasInfusedProperty(pedestalStack, AlchemancyProperties.TINTED))
						{
							Integer[] colors = AlchemancyProperties.TINTED.get().getData(pedestalStack);
							result.set(result.get().withColor(colors.length > 0 ? colors[0] : TintedProperty.DEFAULT_COLOR));
						}

						return result.get();
					});

					setName = true;
					grid.consumeItem(pedestal);
					name.append(pedestalName);
				}

			}

			if(setName)
				stack.set(DataComponents.CUSTOM_NAME, name);

			return stack;
		};
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
		return AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_CUSTOM_NAME.get();
	}

	public static class Serializer implements RecipeSerializer<ForgeCustomNameRecipe> {

		private static final MapCodec<ForgeCustomNameRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
				Ingredient.CODEC.fieldOf("infusable").forGetter(recipe -> recipe.ingredient),
				Property.CODEC.optionalFieldOf("bold").forGetter(recipe -> recipe.bold),
				Property.CODEC.optionalFieldOf("italic").forGetter(recipe -> recipe.italic),
				Property.CODEC.optionalFieldOf("strikethrough").forGetter(recipe -> recipe.strikethrough),
				Property.CODEC.optionalFieldOf("underlined").forGetter(recipe -> recipe.underlined),
				Property.CODEC.optionalFieldOf("obfuscated").forGetter(recipe -> recipe.obfuscated),
				Codec.BOOL.optionalFieldOf("can_tint").forGetter(recipe -> Optional.of(recipe.canTint))
		).apply(instance, ForgeCustomNameRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, ForgeCustomNameRecipe> STREAM_CODEC = StreamCodec.of((encoder, recipe) ->
				{
					Ingredient.CONTENTS_STREAM_CODEC.encode(encoder, recipe.ingredient);
					ByteBufCodecs.optional(Property.STREAM_CODEC).encode(encoder, recipe.bold);
					ByteBufCodecs.optional(Property.STREAM_CODEC).encode(encoder, recipe.italic);
					ByteBufCodecs.optional(Property.STREAM_CODEC).encode(encoder, recipe.strikethrough);
					ByteBufCodecs.optional(Property.STREAM_CODEC).encode(encoder, recipe.underlined);
					ByteBufCodecs.optional(Property.STREAM_CODEC).encode(encoder, recipe.obfuscated);
					encoder.writeBoolean(recipe.canTint);
				},
				(decoder) ->
						new ForgeCustomNameRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(decoder),
								ByteBufCodecs.optional(Property.STREAM_CODEC).decode(decoder),
								ByteBufCodecs.optional(Property.STREAM_CODEC).decode(decoder),
								ByteBufCodecs.optional(Property.STREAM_CODEC).decode(decoder),
								ByteBufCodecs.optional(Property.STREAM_CODEC).decode(decoder),
								ByteBufCodecs.optional(Property.STREAM_CODEC).decode(decoder),
								Optional.of(decoder.readBoolean()))
		);

		@Override
		public MapCodec<ForgeCustomNameRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ForgeCustomNameRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
