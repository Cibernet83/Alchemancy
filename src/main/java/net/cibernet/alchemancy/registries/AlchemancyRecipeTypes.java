package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.*;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<ForgePropertyRecipe, Holder<Property>>> ALCHEMANCY_FORGE_PROPERTY = REGISTRY.register("forged_property", () ->
				new AbstractForgeRecipe.Serializer<>(Property.CODEC, Property.STREAM_CODEC, ForgePropertyRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<PropertyInteractionRecipe, List<Holder<Property>>>> PROPERTY_INTERACTION = REGISTRY.register("property_interaction", () ->
				new AbstractForgeRecipe.Serializer<>(Property.LIST_CODEC, PropertyInteractionRecipe.PROPERTY_LIST_STREAM_CODEC, PropertyInteractionRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, AbstractForgeRecipe.Serializer<PropertyWarpRecipe, List<Holder<Property>>>> PROPERTY_WARP = REGISTRY.register("property_warp", () ->
				new AbstractForgeRecipe.Serializer<>(Property.LIST_CODEC, PropertyInteractionRecipe.PROPERTY_LIST_STREAM_CODEC, PropertyWarpRecipe::new));
		public static final DeferredHolder<RecipeSerializer<?>, DormantPropertyInfusionRecipe.Serializer> DORMANT_PROPERTIES = REGISTRY.register("dormant_properties", DormantPropertyInfusionRecipe.Serializer::new);
		public static final DeferredHolder<RecipeSerializer<?>, ForgeCustomNameRecipe.Serializer> ALCHEMANCY_FORGE_CUSTOM_NAME = REGISTRY.register("forged_custom_name", ForgeCustomNameRecipe.Serializer::new);
	}
}
