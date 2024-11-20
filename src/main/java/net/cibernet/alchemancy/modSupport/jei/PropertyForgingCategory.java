package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static net.cibernet.alchemancy.modSupport.jei.AlchemancyJeiPlugin.PROPERTY_FORGING;

public class PropertyForgingCategory extends AbstractForgingRecipe<ForgePropertyRecipe>
{
	private static final Component TITLE = Component.translatable("recipe.alchemancy.property_forging");

	public PropertyForgingCategory(IGuiHelper helper) {
		super(helper);
	}

	@Override
	public ItemStack getOutput(ForgePropertyRecipe recipe)
	{
		return recipe.getResult().isEmpty() ? ItemStack.EMPTY : InfusedPropertiesHelper.createPropertyCapsule(recipe.getResult());
	}

	@Override
	public RecipeType<ForgePropertyRecipe> getRecipeType() {
		return PROPERTY_FORGING;
	}

	@Override
	public Component getTitle() {
		return TITLE;
	}
}
