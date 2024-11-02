package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.crafting.ingredient.WaterBottleIngredient;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber
public class RecipeEventHandler
{
	@SubscribeEvent
	public static void onBrewingRegistry(RegisterBrewingRecipesEvent event)
	{
		event.getBuilder().addRecipe(new WaterBottleIngredient().toVanilla(), Ingredient.of(AlchemancyItems.BLAZEBLOOM.asItem()), AlchemancyItems.ALCHEMICAL_EXTRACT.toStack());
	}
}
