package net.cibernet.alchemancy.modSupport.patchouli;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.function.UnaryOperator;

public class TintedExampleRecipeComponent extends IngredientRingComponentBase
{
	@Override
	public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY)
	{
		super.render(graphics, context, pticks, mouseX, mouseY);
		context.renderIngredient(graphics, x + RADIUS, y + RADIUS, mouseX, mouseY, Ingredient.EMPTY);
	}

	@Override
	public void build(int componentX, int componentY, int pageNum)
	{
		super.build(componentX, componentY, pageNum);
		ingredients = List.of(Ingredient.of(Items.RED_DYE), Ingredient.of(Items.BLUE_DYE));
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {

	}
}
