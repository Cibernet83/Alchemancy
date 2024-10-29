package net.cibernet.alchemancy.events.handler;

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
		//TODO make ICustomIngredient to have the recipe only accept water bottles
		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(AlchemancyItems.BLAZEBLOOM.asItem()), AlchemancyItems.ALCHEMICAL_EXTRACT.toStack());
	}
}
