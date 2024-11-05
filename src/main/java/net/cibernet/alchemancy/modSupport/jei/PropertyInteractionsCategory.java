package net.cibernet.alchemancy.modSupport.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.cibernet.alchemancy.crafting.PropertyInteractionRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static net.cibernet.alchemancy.modSupport.jei.AlchemancyJeiPlugin.PROPERTY_INTERACTIONS;

public class PropertyInteractionsCategory extends AbstractForgingRecipe<PropertyInteractionRecipe>
{
	private static final Component TITLE = Component.translatable("recipe.alchemancy.property_interactions");
	private final IDrawable icon;

	public PropertyInteractionsCategory(IGuiHelper helper) {
		super(helper);
		icon = helper.createDrawableItemStack(InfusedPropertiesHelper.createPropertyCapsule(AlchemancyProperties.RANDOM));
	}

	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public ItemStack getOutput(PropertyInteractionRecipe recipe)
	{
		return recipe.getResult().isEmpty() ? ItemStack.EMPTY : InfusedPropertiesHelper.createPropertyCapsule(recipe.getResult());
	}

	@Override
	public RecipeType<PropertyInteractionRecipe> getRecipeType()
	{
		return PROPERTY_INTERACTIONS;
	}

	@Override
	public Component getTitle() {
		return TITLE;
	}
}
