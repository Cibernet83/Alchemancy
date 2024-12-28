package net.cibernet.alchemancy.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgeItemRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.crafting.ItemTransmutationRecipe;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.util.TriState;

import java.util.List;
import java.util.Optional;

public record ForgeRecipePredicate(Optional<List<Holder<Property>>> outputProperties, Optional<ItemPredicate> outputItem, Optional<ResourceLocation> recipeKey, Optional<ResourceLocation> recipeType)
{
	public static final Codec<ForgeRecipePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Property.LIST_CODEC.optionalFieldOf("properties").forGetter(ForgeRecipePredicate::outputProperties),
			ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ForgeRecipePredicate::outputItem),
			ResourceLocation.CODEC.optionalFieldOf("recipe_id").forGetter(ForgeRecipePredicate::recipeKey),
			ResourceLocation.CODEC.optionalFieldOf("recipe_type").forGetter(ForgeRecipePredicate::recipeType)
	).apply(instance, ForgeRecipePredicate::new));

	public boolean matches(RecipeHolder<AbstractForgeRecipe<?>> recipe, ForgeRecipeGrid grid)
	{
		if(outputItem().isPresent() && recipe.value().getResult() instanceof ItemStack recipeResult)
			return outputItem.get().test(recipeResult);

		if(recipeKey.isPresent() && recipe.id().equals(recipeKey.get()))
			return true;

		ResourceLocation type = BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.value().getSerializer());
		TriState matches = recipe.value().matches(this, grid);

		return recipeType.map(resourceLocation -> matches != TriState.FALSE && resourceLocation.equals(type)).orElseGet(() -> matches == TriState.TRUE);
	}
}
