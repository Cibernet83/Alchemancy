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
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class ItemTransmutationCategory implements IRecipeCategory<ItemTransmutationRecipe>
{
	private final IDrawable icon;
	private final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "textures/gui/jei/conversion_arrow.png");
	private final Component TITLE = Component.translatable("recipe.alchemancy.item_transmutation");

	protected ItemTransmutationCategory(IGuiHelper helper)
	{
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, AlchemancyItems.ALCHEMANCY_FORGE.toStack());
	}

	@Override
	public RecipeType<ItemTransmutationRecipe> getRecipeType()
	{
		return AlchemancyJeiPlugin.TRANSMUTATION;
	}

	@Override
	public Component getTitle() {
		return Component.literal("Item Transmutation");
	}

	@Override
	public @Nullable IDrawable getIcon() {
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
	public void draw(ItemTransmutationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
	{
		guiGraphics.blit(TEXTURE_LOCATION, 16, 0, 0, 0, 32, 16, 32, 16);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ItemTransmutationRecipe recipe, IFocusGroup focuses)
	{
		Optional<Ingredient> catalystIngredient = recipe.getCatalyst();

		if(catalystIngredient.isPresent() && recipe.getCatalystName().isPresent())
		{
			Component component = Component.literal(recipe.getCatalystName().get());
			ItemStack[] catalystItems = catalystIngredient.get().getItems();
			for (ItemStack catalystItem : catalystItems) {
				if(!catalystItem.isEmpty())
					catalystItem.set(DataComponents.CUSTOM_NAME, component);
			}
			builder.addInputSlot(0, 0).addItemStacks(Arrays.stream(catalystItems).toList());
		}
		else builder.addInputSlot(0, 0).addIngredients(catalystIngredient.orElse(Ingredient.EMPTY));
		builder.addOutputSlot(48, 0).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
	}
}
