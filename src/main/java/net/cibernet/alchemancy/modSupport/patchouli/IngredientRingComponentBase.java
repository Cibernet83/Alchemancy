package net.cibernet.alchemancy.modSupport.patchouli;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;

import java.util.List;

public abstract class IngredientRingComponentBase implements ICustomComponent
{

	transient List<Ingredient> ingredients;
	protected transient int x, y;

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX;
		y = componentY;
	}

	protected static final int RADIUS = 24;

	@Override
	public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY)
	{
		int i = 0;
		float totalSize = ingredients.size();

		for (Ingredient infusable : ingredients)
		{
			context.renderIngredient(graphics,
					x + RADIUS + (int) (RADIUS * Mth.sin(Mth.PI + Mth.TWO_PI * (i / totalSize))),
					y + RADIUS + (int) (RADIUS * Mth.cos(Mth.PI + Mth.TWO_PI * (i / totalSize))),
					mouseX, mouseY, infusable);
			i++;
		}
	}
}
