package net.cibernet.alchemancy.modSupport.patchouli;

import net.cibernet.alchemancy.crafting.ItemTransmutationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class TransmutationComponentProcessor implements IComponentProcessor
{
	private ItemTransmutationRecipe recipe;
	private boolean hasTitle;

	@Override
	public void setup(Level level, IVariableProvider variables)
	{
		String key = variables.get("recipe", level.registryAccess()).asString();
		if(level.getRecipeManager().byKey(ResourceLocation.parse(key)).orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")).value() instanceof ItemTransmutationRecipe r)
			this.recipe = r;
		else throw new IllegalArgumentException(key + " is not a valid transmutation recipe");

		hasTitle = variables.has("title");

	}

	@Override
	public IVariable process(Level level, String key)
	{
		if(key.equals("input"))
			return IVariable.from(recipe.getCatalyst().orElse(Ingredient.EMPTY), level.registryAccess());
		if(key.equals("output"))
			return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());

		if(!hasTitle && key.equals("title"))
			return IVariable.from(recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());

		return null;
	}
}
