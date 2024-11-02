package net.cibernet.alchemancy.crafting;

import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.essence.Essence;
import net.cibernet.alchemancy.properties.Property;
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
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractForgeRecipe<RESULT> implements Recipe<ForgeRecipeGrid>
{
	public static int MIN_PRIORITY = 0;
	final Optional<Ingredient> catalyst;
	final Optional<String> catalystName;
	final List<Ingredient> infusables;
	final List<EssenceContainer> essences;
	final List<Holder<Property>> infusedProperties;

	protected AbstractForgeRecipe(Optional<Ingredient> catalyst, Optional<String> catalystName, List<EssenceContainer> essences, List<Ingredient> infusables, List<Holder<Property>> infusedProperties) {
		this.catalyst = catalyst;
		this.catalystName = catalystName;
		this.infusables = infusables;
		this.essences = essences;
		this.infusedProperties = infusedProperties;

		if(MIN_PRIORITY > getPriority())
			MIN_PRIORITY = getPriority();
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level)
	{
		return  (catalyst.isEmpty() || catalyst.get().test(input.getCurrentOutput())) &&
				(catalystName.isEmpty() || input.getCurrentOutput().getDisplayName().getString().equalsIgnoreCase(catalystName.get())) &&
				input.testInfusables(infusables, false) &&
				input.testEssences(essences, false) &&
				input.testProperties(infusedProperties, false);
	}


	@Override
	public ItemStack assemble(ForgeRecipeGrid input, HolderLookup.Provider registries)
	{
		input.testInfusables(infusables, true);
		input.testEssences(essences, true);
		input.testProperties(infusedProperties, true);

		return processResult().apply(input, registries, input.getCurrentOutput());
	}

	public int getRecipeCompareValue(ForgeRecipeGrid grid)
	{
		return grid.getRecipeCompareValue(this, infusables, essences, getPriority());
	}

	/** Determines at what point in the infusion process the recipe occurs, the lower the number the higher the priority
	 */
	public int getPriority()
	{
		return 0;
	}

	public abstract TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult();

	public boolean isTransmutation()
	{
		return false;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return true;
	}

	@Override
	public RecipeType<?> getType() {
		return AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get();
	}

	protected abstract RESULT getResult();

	public boolean isEmpty() {
		return infusables.isEmpty() && essences.isEmpty();
	}

	public static class Serializer<T extends AbstractForgeRecipe<RESULT>, RESULT> implements RecipeSerializer<T>
	{
		private final MapCodec<T> CODEC;

		private final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC;

		protected static final Codec<EssenceContainer> ESSENCE_CONTAINER_CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						Essence.CODEC.fieldOf("type").forGetter(container -> Holder.direct(container.getEssence())),
						Codec.INT.fieldOf("amount").forGetter(EssenceContainer::getAmount)
				).apply(instance, (essence, amount) -> new EssenceContainer(essence.value(), amount, amount)));

		protected static final StreamCodec<RegistryFriendlyByteBuf, EssenceContainer> ESSENCE_CONTAINER_STREAM_CODEC = StreamCodec.of(
				(encode, container) ->
				{
					encode.writeInt(container.getAmount());
					ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey()).encode(encode, encode.registryAccess().holder(ResourceKey.create(AlchemancyEssence.REGISTRY.getRegistryKey(), container.getEssence().getKey())).get());
				},
				(decode) ->
				{
					int amount = decode.readInt();
					return new EssenceContainer(ByteBufCodecs.holderRegistry(AlchemancyEssence.REGISTRY.getRegistryKey()).decode(decode).value(), amount, amount);
				}
		);

		public Serializer(Codec<RESULT> resultCodec, StreamCodec<RegistryFriendlyByteBuf, RESULT> streamCodec, Function6<Optional<Ingredient>, Optional<String>, List<EssenceContainer>, List<Ingredient>, List<Holder<Property>>, RESULT, T> constructor)
		{
			this(resultCodec.fieldOf("result"), streamCodec, constructor);
		}

		public Serializer(MapCodec<RESULT> resultCodec, StreamCodec<RegistryFriendlyByteBuf, RESULT> streamCodec, Function6<Optional<Ingredient>, Optional<String>, List<EssenceContainer>, List<Ingredient>, List<Holder<Property>>, RESULT, T> constructor)
		{

			CODEC = RecordCodecBuilder.mapCodec((instance) ->  instance.group(
					Ingredient.CODEC.optionalFieldOf("catalyst").forGetter((recipe) -> recipe.catalyst),
					Codec.STRING.optionalFieldOf("catalyst_name").forGetter(recipe -> recipe.catalystName),
					Codec.list(ESSENCE_CONTAINER_CODEC).fieldOf("essences").orElse(List.of()).forGetter(recipe -> recipe.essences),
					Codec.list(Ingredient.CODEC).fieldOf("infusables").orElse(List.of()).forGetter(recipe -> recipe.infusables),
					Property.LIST_CODEC.fieldOf("properties").orElse(List.of()).forGetter(recipe -> recipe.infusedProperties),
					resultCodec.forGetter(AbstractForgeRecipe::getResult)
			).apply(instance, constructor));

			STREAM_CODEC = StreamCodec.of(
					(encode, recipe) ->
					{

						encode.writeInt(recipe.essences.size());
						for (EssenceContainer essence : recipe.essences)
							ESSENCE_CONTAINER_STREAM_CODEC.encode(encode, essence);
						encode.writeInt(recipe.infusables.size());
						for (Ingredient infusable : recipe.infusables)
							Ingredient.CONTENTS_STREAM_CODEC.encode(encode, infusable);
						encode.writeInt(recipe.infusedProperties.size());
						for (Holder<Property> propertyHolder : recipe.infusedProperties)
							Property.STREAM_CODEC.encode(encode, propertyHolder);

						encode.writeBoolean(recipe.catalyst.isPresent());
						recipe.catalyst.ifPresent(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.encode(encode, ingredient));

						encode.writeBoolean(recipe.catalystName.isPresent());
						recipe.catalystName.ifPresent(name -> ByteBufCodecs.STRING_UTF8.encode(encode, name));


						streamCodec.encode(encode, recipe.getResult());
					},
					(decode) ->
					{
						int listSize = decode.readInt();
						ArrayList<EssenceContainer> essences = new ArrayList<>();
						for(int i = 0; i < listSize; i++)
							essences.add(ESSENCE_CONTAINER_STREAM_CODEC.decode(decode));

						listSize = decode.readInt();
						ArrayList<Ingredient> infusables = new ArrayList<>();
						for(int i = 0; i < listSize; i++)
							infusables.add(Ingredient.CONTENTS_STREAM_CODEC.decode(decode));

						listSize = decode.readInt();
						ArrayList<Holder<Property>> properties = new ArrayList<>();
						for(int i = 0; i < listSize; i++)
							properties.add(Property.STREAM_CODEC.decode(decode));

						Optional<Ingredient> catalyst = decode.readBoolean() ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(decode)) : Optional.empty();
						Optional<String> catalystName = decode.readBoolean() ? Optional.of(ByteBufCodecs.STRING_UTF8.decode(decode)) : Optional.empty();

						return constructor.apply(catalyst, catalystName, essences, infusables, properties, streamCodec.decode(decode));
					}
			);
		}


		@Override
		public MapCodec<T> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
