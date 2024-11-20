package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractForgingRecipe<T extends AbstractForgeRecipe<?>> implements IRecipeCategory<T>
{
	private final ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/gui/jei/conversion_arrow.png");
	private final ResourceLocation DIAGRAM = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/gui/jei/forge_diagram.png");
	private final ResourceLocation OUTPUT = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/gui/jei/property_output_slot.png");
	private final IDrawable icon;
	protected static final int RADIUS = 24;

	public AbstractForgingRecipe(IGuiHelper helper)
	{
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, AlchemancyItems.INFUSION_PEDESTAL.toStack());
	}

	@Override
	public @Nullable IDrawable getIcon() {
		return icon;
	}

	@Override
	public int getWidth() {
		return RADIUS * 2 + 64;
	}

	@Override
	public int getHeight() {
		return RADIUS * 2 + 16;
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		guiGraphics.blit(ARROW, RADIUS * 2 + 16, RADIUS, 0, 0, 32, 16, 32, 16);
		guiGraphics.blit(DIAGRAM, 8, 8, 0, 0, 48, 48, 48, 48);


		if(getOutput(recipe).isEmpty())
			guiGraphics.blit(OUTPUT, RADIUS * 2 + 48, RADIUS, 0, 0, 16, 16, 16, 16);
	}

	public abstract ItemStack getOutput(T recipe);

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
	{
		int xOff = RADIUS, yOff = RADIUS;

		ArrayList<Ingredient> ingredients = new ArrayList<>(recipe.getInfusables());
		List<ItemStack> propertyCapsules = recipe.getInfusedProperties().stream().map(InfusedPropertiesHelper::createPropertyIngredient).toList();
		float totalSize = ingredients.size() + propertyCapsules.size();

		for(int i = 0; i < ingredients.size(); i++)
			builder.addInputSlot(
					xOff + (int) (RADIUS * Mth.sin(Mth.PI + Mth.TWO_PI * (i / totalSize))),
					yOff + (int) (RADIUS * Mth.cos(Mth.PI + Mth.TWO_PI * (i / totalSize))))
					.addIngredients(ingredients.get(i));
		for(int i = 0; i < propertyCapsules.size(); i++)
			builder.addInputSlot(
					xOff + (int) (RADIUS * Mth.sin(Mth.PI + Mth.TWO_PI * ((i + ingredients.size()) / totalSize))),
					yOff + (int) (RADIUS * Mth.cos(Mth.PI + Mth.TWO_PI * ((i + ingredients.size()) / totalSize))))
					.addItemStack(propertyCapsules.get(i));

		if(recipe.getCatalyst().isPresent())
			builder.addInputSlot(xOff, yOff).addIngredients(recipe.getCatalyst().get());

		builder.addOutputSlot(xOff + RADIUS + 48, yOff).addItemStack(getOutput(recipe));
	}
}
