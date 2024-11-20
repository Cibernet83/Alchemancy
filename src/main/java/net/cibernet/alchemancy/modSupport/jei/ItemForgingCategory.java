package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.cibernet.alchemancy.crafting.ForgeItemRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static net.cibernet.alchemancy.modSupport.jei.AlchemancyJeiPlugin.ITEM_FORGING;

public class ItemForgingCategory extends AbstractForgingRecipe<ForgeItemRecipe>
{
	private static final Component TITLE = Component.translatable("recipe.alchemancy.item_forging");

	public ItemForgingCategory(IGuiHelper helper) {
		super(helper);
	}

	@Override
	public ItemStack getOutput(ForgeItemRecipe recipe)
	{
		return recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
	}

	@Override
	public RecipeType<ForgeItemRecipe> getRecipeType() {
		return ITEM_FORGING;
	}

	@Override
	public Component getTitle() {
		return TITLE;
	}
}
