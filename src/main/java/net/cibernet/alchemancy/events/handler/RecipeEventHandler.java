package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber
public class RecipeEventHandler
{
	@SubscribeEvent
	public static void onBrewingRegistry(RegisterBrewingRecipesEvent event)
	{
		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(AlchemancyItems.BLAZEBLOOM.asItem()), AlchemancyItems.BLAZING_SUBSTANCE.toStack());
		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(AlchemancyItems.CLOUDELION.asItem()), AlchemancyItems.GUSTY_SUBSTANCE.toStack());
		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(AlchemancyItems.GLOWSHROOM.asItem()), AlchemancyItems.EARTHEN_SUBSTANCE.toStack());
		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(AlchemancyItems.HYDROLILY.asItem()), AlchemancyItems.TIDAL_SUBSTANCE.toStack());


		event.getBuilder().addRecipe(Ingredient.of(Items.POTION.asItem()), Ingredient.of(Items.CRIMSON_FUNGUS), AlchemancyItems.BLAZING_SUBSTANCE.toStack());
	}
}
