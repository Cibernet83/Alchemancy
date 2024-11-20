package net.cibernet.alchemancy.crafting;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;

import static net.cibernet.alchemancy.registries.AlchemancyRecipeTypes.Serializers.PLAYER_HEAD_TRANSMUTATION;

public class PlayerHeadTransmutationRecipe extends ItemTransmutationRecipe
{
	public PlayerHeadTransmutationRecipe()
	{
		super(Optional.of(Ingredient.of(Items.PLAYER_HEAD)), Optional.empty(), List.of(), List.of(), List.of(), Items.PLAYER_HEAD.getDefaultInstance());
	}

	@Override
	public Optional<String> getCatalystName()
	{
		if(ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer() instanceof IntegratedServer && Minecraft.getInstance().player != null)
			return Optional.of(Minecraft.getInstance().player.getGameProfile().getName());
		return super.getCatalystName();
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries)
	{
		ItemStack result = super.getResultItem(registries).copy();
		if(ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer() instanceof IntegratedServer && Minecraft.getInstance().player != null)
			result.set(DataComponents.PROFILE, new ResolvableProfile(Minecraft.getInstance().player.getGameProfile()));
		return result;
	}

	@Override
	public boolean matches(ForgeRecipeGrid input, Level level)
	{
		return input.getCurrentOutput().has(DataComponents.CUSTOM_NAME) && super.matches(input, level);
	}

	@Override
	public TriFunction<ForgeRecipeGrid, HolderLookup.Provider, ItemStack, ItemStack> processResult()
	{
		return (input, registries, output) -> {

			ItemStack result = this.result.copy();

			if(ItemStack.isSameItem(result, input.getCurrentOutput()))
				result.setCount(result.getCount() + input.getCurrentOutput().getCount() - 1);


			String playerName = toPlayerName(output.get(DataComponents.CUSTOM_NAME).getString());
			result.set(DataComponents.PROFILE, new ResolvableProfile(Optional.of(playerName), Optional.empty(), new PropertyMap()));

			result.set(AlchemancyItems.Components.INFUSED_PROPERTIES, output.get(AlchemancyItems.Components.INFUSED_PROPERTIES));
			result.set(AlchemancyItems.Components.PROPERTY_DATA, output.get(AlchemancyItems.Components.PROPERTY_DATA));
			return result;
		};
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PLAYER_HEAD_TRANSMUTATION.get();
	}

	public static String toPlayerName(String playerName)
	{
		return playerName.substring(0, Math.min(playerName.length(), 16)).chars().filter(c -> !(c <= 32 || c >= 127)).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		//return playerName.length() <= 16 && playerName.chars().filter(p_332111_ -> p_332111_ <= 32 || p_332111_ >= 127).findAny().isEmpty();
	}

	public static class Serializer implements RecipeSerializer<PlayerHeadTransmutationRecipe> {
		private final MapCodec<PlayerHeadTransmutationRecipe> codec;
		private final StreamCodec<RegistryFriendlyByteBuf, PlayerHeadTransmutationRecipe> streamCodec;

		private final PlayerHeadTransmutationRecipe INSTANCE = new PlayerHeadTransmutationRecipe();

		public Serializer() {
			this.codec = RecordCodecBuilder.mapCodec(
					p_311736_ -> p_311736_.point(INSTANCE)
			);
			this.streamCodec = StreamCodec.unit(INSTANCE);
		}

		@Override
		public MapCodec<PlayerHeadTransmutationRecipe> codec() {
			return this.codec;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PlayerHeadTransmutationRecipe> streamCodec() {
			return this.streamCodec;
		}

	}
}
