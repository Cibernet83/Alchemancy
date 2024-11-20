package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.ItemTransmutationRecipe;
import net.cibernet.alchemancy.crafting.PropertyWarpRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class PropertyWarpingCategory implements IRecipeCategory<PropertyWarpRecipe>
{
	private final IDrawable icon;
	private final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/gui/jei/conversion_arrow.png");
	private final Component TITLE = Component.translatable("recipe.alchemancy.property_warping");

	protected PropertyWarpingCategory(IGuiHelper helper)
	{
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.WARPED_WART_BLOCK.getDefaultInstance());
	}

	@Override
	public RecipeType<PropertyWarpRecipe> getRecipeType()
	{
		return AlchemancyJeiPlugin.PROPERTY_WARPING;
	}

	@Override
	public Component getTitle()
	{
		return TITLE;
	}

	@Override
	public @Nullable IDrawable getIcon()
	{
		return icon;
	}

	@Override
	public int getWidth() {
		return 64;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public void draw(PropertyWarpRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

		guiGraphics.blit(TEXTURE_LOCATION, 16, 0, 0, 0, 32, 16, 32, 16);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, PropertyWarpRecipe recipe, IFocusGroup focuses)
	{
		builder.addInputSlot(0, 0).addItemStack(InfusedPropertiesHelper.createPropertyIngredient(recipe.getInfusedProperties()));
		builder.addOutputSlot(48, 0).addItemStack(InfusedPropertiesHelper.createPropertyIngredient(recipe.getResult()));
	}
}
