package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.*;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

public class AlchemancyRecipeTypes
{
	public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<RecipeType<?>, RecipeType<EssenceExtractionRecipe>> ESSENCE_EXTRACTION = REGISTRY.register("essence_extraction", () -> new RecipeType<>() {});
	public static final DeferredHolder<RecipeType<?>, RecipeType<AbstractForgeRecipe<?>>> ALCHEMANCY_FORGE = REGISTRY.register("alchemancy_forge", () -> new RecipeType<>() {});

	public static class Serializers
	{
		public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Alchemancy.MODID);

		public static final DeferredHolder<RecipeSerializer<?>, EssenceExtractionRecipe.Serializer> ESSENCE_EXTRACTION = REGISTRY.register("essence_extraction", EssenceExtractionRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<ForgeItemRecipe, ItemStack>> ALCHEMANCY_FORGE_ITEM = REGISTRY.register("forged_item", () ->
				new AbstractForgeRecipe.Serializer<>(ItemStack.CODEC, ItemStack.STREAM_CODEC, ForgeItemRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<ItemTransmutationRecipe, ItemStack>> ITEM_TRANSMUTATION = REGISTRY.register("item_transmutation", () ->
				new AbstractForgeRecipe.Serializer<>(ItemStack.CODEC, ItemStack.STREAM_CODEC, ItemTransmutationRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, PlayerHeadTransmutationRecipe.Serializer> PLAYER_HEAD_TRANSMUTATION = REGISTRY.register("player_head_transmutation", PlayerHeadTransmutationRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<ForgePropertyRecipe, List<Holder<Property>>>> ALCHEMANCY_FORGE_PROPERTY = REGISTRY.register("forged_property", () ->
				new AbstractForgeRecipe.Serializer<>(Property.LIST_CODEC, ForgePropertyRecipe.PROPERTY_LIST_STREAM_CODEC, ForgePropertyRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<PropertyWarpRecipe, List<Holder<Property>>>> PROPERTY_WARP = REGISTRY.register("property_warp", () ->
				new AbstractForgeRecipe.Serializer<>(Property.LIST_CODEC, ForgePropertyRecipe.PROPERTY_LIST_STREAM_CODEC, PropertyWarpRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, DormantPropertyInfusionRecipe.Serializer> DORMANT_PROPERTIES = REGISTRY.register("dormant_properties", DormantPropertyInfusionRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<ForgeRemovePropertiesRecipe, Optional<ItemStack>>> REMOVE_PROPERTIES = REGISTRY.register("remove_properties", () ->
				new AbstractForgeRecipe.Serializer<>(ItemStack.CODEC.optionalFieldOf("result"), ByteBufCodecs.optional(ItemStack.STREAM_CODEC), ForgeRemovePropertiesRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, ForgeCustomNameRecipe.Serializer> ALCHEMANCY_FORGE_CUSTOM_NAME = REGISTRY.register("forged_custom_name", ForgeCustomNameRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, ForgeLoreRecipe.Serializer> ALCHEMANCY_FORGE_CUSTOM_LORE = REGISTRY.register("forged_custom_lore", ForgeLoreRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, ForgeChromaTintingProperty.Serializer> ALCHEMANCY_FORGE_CHROMA_TINTING = REGISTRY.register("forged_chroma_tinting", ForgeChromaTintingProperty.Serializer::new);

		public static final DeferredHolder<RecipeSerializer<?>, RestoreClayMoldSmeltingRecipe.Serializer> RESTORE_CLAY_MOLD_SMELTING = REGISTRY.register("restore_clay_mold_smelting", RestoreClayMoldSmeltingRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RestoreClayMoldCraftingRecipe>> RESTORE_CLAY_MOLD_CRAFTING = REGISTRY.register("restore_clay_mold_crafting", () -> new SimpleCraftingRecipeSerializer<>(RestoreClayMoldCraftingRecipe::new));
	}
}
