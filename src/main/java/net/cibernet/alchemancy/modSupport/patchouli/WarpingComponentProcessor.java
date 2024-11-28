package net.cibernet.alchemancy.modSupport.patchouli;

import net.cibernet.alchemancy.crafting.PropertyWarpRecipe;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.stream.Collectors;

public class WarpingComponentProcessor implements IComponentProcessor
{
	private PropertyWarpRecipe recipe;
	private boolean hasTitle;

	@Override
	public void setup(Level level, IVariableProvider variables)
	{
		String key = variables.get("recipe", level.registryAccess()).asString();
		if(level.getRecipeManager().byKey(ResourceLocation.parse(key)).orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")).value() instanceof PropertyWarpRecipe r)
			this.recipe = r;
		else throw new IllegalArgumentException(key + " is not a valid warping recipe");

		hasTitle = variables.has("title");

	}

	@Override
	public IVariable process(Level level, String key)
	{
		if(key.equals("input"))
			return IVariable.from(InfusedPropertiesHelper.createPropertyIngredient(recipe.getInfusedProperties()), level.registryAccess());
		if(key.equals("output"))
			return IVariable.from(InfusedPropertiesHelper.createPropertyIngredient(recipe.getResult()), level.registryAccess());

		if(!hasTitle && key.equals("title"))
		{
			return IVariable.wrap(recipe.getResult().stream()
					.map(propertyHolder -> propertyHolder.value().getDisplayText(ItemStack.EMPTY).getString())
					.collect(Collectors.joining(" and ")), level.registryAccess());
		}

		return null;
	}
}
