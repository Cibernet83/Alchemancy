package net.cibernet.alchemancy.modSupport.patchouli;

import com.google.gson.annotations.SerializedName;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class ItemInfusionRecipeComponent extends IngredientRingComponentBase
{

	transient AbstractForgeRecipe<?> recipe;


	@SerializedName("recipe")
	public String recipeName;

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries)
	{
		recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();

		Level level = Minecraft.getInstance().level;
		if(level != null && level.getRecipeManager().byKey(ResourceLocation.parse(recipeName)).orElseThrow(() -> new IllegalArgumentException("recipe " + recipeName + " does not exist")).value() instanceof AbstractForgeRecipe r)
			this.recipe = r;
		else throw new IllegalArgumentException(recipeName + " is not a valid recipe");
	}

	@Override
	public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY)
	{
		super.render(graphics, context, pticks, mouseX, mouseY);
		context.renderIngredient(graphics, x + RADIUS, y + RADIUS, mouseX, mouseY, recipe.getCatalyst().orElse(Ingredient.EMPTY));
	}

	@Override
	public void build(int componentX, int componentY, int pageNum)
	{
		super.build(componentX, componentY, pageNum);
		ingredients = new ArrayList<>(recipe.getInfusables());
		ingredients.addAll(recipe.getInfusedProperties().stream().map(propertyHolder -> Ingredient.of(InfusedPropertiesHelper.createPropertyIngredient(propertyHolder))).toList());
	}
}
