package net.cibernet.alchemancy.modSupport.patchouli;

import net.cibernet.alchemancy.crafting.AbstractForgeRecipe;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.stream.Collectors;

public class InfusionRecipeComponentProcessor implements IComponentProcessor
{
	private AbstractForgeRecipe<?> recipe;
	private boolean hasTitle;
	//private Optional<Boolean> linkRecipe;

	@Override
	public void setup(Level level, IVariableProvider variables)
	{
		String key = variables.get("recipe", level.registryAccess()).asString();
		if(level.getRecipeManager().byKey(ResourceLocation.parse(key)).orElseThrow(() -> new IllegalArgumentException("recipe " + key + " does not exist")).value() instanceof AbstractForgeRecipe r)
			this.recipe = r;
		else throw new IllegalArgumentException(key + " is not a valid infusion recipe");
		hasTitle = variables.has("title");
		//linkRecipe = variables.has("link_recipe") ? Optional.of(variables.get("link_recipe", level.registryAccess()).asBoolean()) : Optional.empty();

	}


	@Override
	public IVariable process(Level level, String key)
	{
		System.out.println("goober key: " + key);

		if(key.equals("catalyst"))
			return IVariable.from(recipe.getCatalyst().orElse(Ingredient.EMPTY), level.registryAccess());
		if(key.equals("output"))
			return IVariable.from(recipe.getResultItem(level.registryAccess()), level.registryAccess());

		//WHY DOESN'T PROCESS WORK WITH BOOLS AAAAAAAAAAAAAA
//		if(key.equals("link_recipe"))
//		{
//			return linkRecipe.map(aBoolean -> IVariable.wrap(aBoolean, level.registryAccess()))
//					.orElseGet(() -> {
//						System.out.println("recipe: " + recipe + " result: " + recipe.getResult() + "links recipe? " + (recipe instanceof ForgeItemRecipe));
//						return IVariable.wrap(recipe instanceof ForgeItemRecipe, level.registryAccess());
//					});
//		}

		if(!hasTitle && key.equals("title"))
		{
			if(recipe instanceof ForgePropertyRecipe recipe1)
				return IVariable.wrap(recipe1.getResult().stream()
						.map(propertyHolder -> propertyHolder.value().getName().getString())
						.collect(Collectors.joining(" and ")), level.registryAccess());

			return IVariable.from(recipe.getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());
		}

		return null;
	}
}
