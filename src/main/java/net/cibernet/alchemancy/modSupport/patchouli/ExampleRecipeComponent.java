package net.cibernet.alchemancy.modSupport.patchouli;

import com.google.gson.annotations.SerializedName;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.IVariable;

import java.util.List;
import java.util.function.UnaryOperator;

public class ExampleRecipeComponent extends IngredientRingComponentBase
{

	transient List<ItemStack> ingredients;
	transient ItemStack input = ItemStack.EMPTY;
	protected transient int x, y;


	@SerializedName("example_type")
	public String exampleType;

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX;
		y = componentY;

		switch (exampleType) {
			case "tinted" ->
					ingredients = List.of(Items.RED_DYE.getDefaultInstance(), Items.BLUE_DYE.getDefaultInstance());
			case "name_tag" -> {
				ItemStack stack = Items.NAME_TAG.getDefaultInstance();
				stack.set(DataComponents.CUSTOM_NAME, Component.literal("Cool Sword"));

				input = Items.IRON_SWORD.getDefaultInstance();
				ingredients = List.of(stack);
			}
			case "name_tag_multiple" -> {
				ItemStack tag1 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.HEAVY);
				CommonUtils.applyChromaTint(tag1, 11546150);
				tag1.set(DataComponents.CUSTOM_NAME, Component.literal("The "));

				ItemStack tag2 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.SLIPPERY);
				CommonUtils.applyChromaTint(tag2, 3847130);
				tag2.set(DataComponents.CUSTOM_NAME, Component.literal("Coolest "));

				ItemStack tag3 = InfusedPropertiesHelper.addProperty(Items.NAME_TAG.getDefaultInstance(), AlchemancyProperties.SLASHING);
				CommonUtils.applyChromaTint(tag3, 16701501);
				tag3.set(DataComponents.CUSTOM_NAME, Component.literal("Sword "));

				ItemStack tag4 = InfusedPropertiesHelper.addProperties(Items.NAME_TAG.getDefaultInstance(), List.of(AlchemancyProperties.MINING, AlchemancyProperties.HEAVY));
				CommonUtils.applyChromaTint(tag4, 8439583);
				tag4.set(DataComponents.CUSTOM_NAME, Component.literal("Ever"));

				input = Items.IRON_SWORD.getDefaultInstance();
				ingredients = List.of(tag1, tag2, tag3, tag4);
			}
			case "lore" -> {

				input = Items.IRON_SWORD.getDefaultInstance();
				ingredients = List.of(Items.WRITTEN_BOOK.getDefaultInstance());
			}
			default -> ingredients = List.of();
		}
	}

	protected static final int RADIUS = 24;

	@Override
	public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY)
	{
		int i = 0;
		float totalSize = ingredients.size();

		for (ItemStack infusable : ingredients)
		{
			context.renderItemStack(graphics,
					x + RADIUS + (int) (RADIUS * Mth.sin(Mth.PI - Mth.TWO_PI * (i / totalSize))),
					y + RADIUS + (int) (RADIUS * Mth.cos(Mth.PI + Mth.TWO_PI * (i / totalSize))),
					mouseX, mouseY, infusable);
			i++;
		}

		context.renderItemStack(graphics, x + RADIUS, y + RADIUS, mouseX, mouseY, input);
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {

		exampleType = lookup.apply(IVariable.wrap(exampleType, registries)).asString();
	}
}
