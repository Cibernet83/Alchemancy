package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.*;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class AlchemancyJeiPlugin implements IModPlugin
{
	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "alchemancy_forge");

	public static final RecipeType<ItemTransmutationRecipe> TRANSMUTATION = RecipeType.create(Alchemancy.MODID, "transmutation", ItemTransmutationRecipe.class);
	public static final RecipeType<PropertyWarpRecipe> PROPERTY_WARPING = RecipeType.create(Alchemancy.MODID, "property_warping", PropertyWarpRecipe.class);
	public static final RecipeType<ForgeItemRecipe> ITEM_FORGING = RecipeType.create(Alchemancy.MODID, "item_forging", ForgeItemRecipe.class);
	public static final RecipeType<ForgePropertyRecipe> PROPERTY_FORGING = RecipeType.create(Alchemancy.MODID, "property_forging", ForgePropertyRecipe.class);
	public static final RecipeType<PropertyInteractionRecipe> PROPERTY_INTERACTIONS = RecipeType.create(Alchemancy.MODID, "property_interactions", PropertyInteractionRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration)
	{
		registration.addRecipeCategories(new ItemTransmutationCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new PropertyWarpingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new ItemForgingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new PropertyForgingCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new PropertyInteractionsCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
	{
		registration.addRecipeCatalysts(TRANSMUTATION, AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE);
		registration.addRecipeCatalysts(PROPERTY_INTERACTIONS, AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE);
		registration.addRecipeCatalysts(ITEM_FORGING, AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE, AlchemancyItems.INFUSION_PEDESTAL);
		registration.addRecipeCatalysts(PROPERTY_FORGING, AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE, AlchemancyItems.INFUSION_PEDESTAL);
		registration.addRecipeCatalysts(PROPERTY_WARPING, AlchemancyItems.ALCHEMANCY_CATALYST, AlchemancyItems.ALCHEMANCY_FORGE);
		registration.addRecipeCatalyst(InfusedPropertiesHelper.createPropertyCapsule(AlchemancyProperties.WARPED), PROPERTY_WARPING);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		List<RecipeHolder<AbstractForgeRecipe<?>>> forgeRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get());

		List<ItemTransmutationRecipe> transmutationRecipes = new ArrayList<>();
		List<PropertyWarpRecipe> propertyWarpRecipes = new ArrayList<>();
		List<ForgeItemRecipe> forgeItemRecipes = new ArrayList<>();
		List<ForgePropertyRecipe> forgePropertyRecipes = new ArrayList<>();
		List<PropertyInteractionRecipe> propertyInteractionRecipes = new ArrayList<>();

		for (RecipeHolder<AbstractForgeRecipe<?>> recipe : forgeRecipes)
		{
			if(
					addTo(transmutationRecipes, ItemTransmutationRecipe.class, recipe) ||
					addToExact(propertyWarpRecipes, PropertyWarpRecipe.class, recipe) ||
					addToExact(forgeItemRecipes, ForgeItemRecipe.class, recipe) ||
					addToExact(forgePropertyRecipes, ForgePropertyRecipe.class, recipe) ||
					addToExact(propertyInteractionRecipes, PropertyInteractionRecipe.class, recipe)
			);
		}

		registration.addRecipes(TRANSMUTATION, transmutationRecipes);
		registration.addRecipes(PROPERTY_WARPING, propertyWarpRecipes);
		registration.addRecipes(ITEM_FORGING, forgeItemRecipes);
		registration.addRecipes(PROPERTY_FORGING, forgePropertyRecipes);
		registration.addRecipes(PROPERTY_INTERACTIONS, propertyInteractionRecipes);
	}

	public <T extends AbstractForgeRecipe<?>> boolean addToExact(List<T> list, Class<T> clazz, RecipeHolder<AbstractForgeRecipe<?>> holder)
	{
		if(clazz.equals(holder.value().getClass()))
		{
			list.add(clazz.cast(holder.value()));
			return true;
		}
		return false;
	}

	public <T extends AbstractForgeRecipe<?>> boolean addTo(List<T> list, Class<T> clazz, RecipeHolder<AbstractForgeRecipe<?>> holder)
	{
		if(clazz.isInstance(holder.value()))
		{
			list.add(clazz.cast(holder.value()));
			return true;
		}
		return false;
	}
}
