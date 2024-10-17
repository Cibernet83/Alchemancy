package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;

public class AssembleProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(user instanceof Player player)
		{
			boolean assimilate = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ASSIMILATING) && AssimilatingProperty.shouldRepair(stack);
			if(!assimilate && stack.getCount() >= stack.getMaxStackSize())
				return;

			HashMap<CraftingRecipe, NonNullList<Integer>> acceptedRecipes = new HashMap<>();

			Level level = player.level();
			RegistryAccess registryAccess = level.registryAccess();

			List<RecipeHolder<CraftingRecipe>> availableRecipes = getRecipesMatchingOutput(stack, level);
			for(RecipeHolder<CraftingRecipe> craftingRecipe : availableRecipes)
			{
				NonNullList<Integer> acceptedSlots = NonNullList.withSize(player.getInventory().items.size(), 0);

				if(!(assimilate || craftingRecipe.value().getResultItem(registryAccess).getCount() + stack.getCount() <= stack.getMaxStackSize()))
					return;

				boolean accepted = true;
				for(Ingredient ingredient : craftingRecipe.value().getIngredients())
				{
					if(ingredient.isEmpty())
						continue;

					boolean hasIngredient = false;
					for (int i = 0; i < player.getInventory().items.size(); i++)
					{
						ItemStack inventoryStack = player.getInventory().items.get(i);
						ItemStack storedItem = AlchemancyProperties.HOLLOW.get().getData(inventoryStack);
						if(!storedItem.isEmpty())
							inventoryStack = storedItem;

						if(stack == inventoryStack)
							continue;

						if(acceptedSlots.get(i) < inventoryStack.getCount() && ingredient.test(inventoryStack))
						{
							hasIngredient = true;
							acceptedSlots.set(i, acceptedSlots.get(i)+1);
							break;
						}
					}

					if(!hasIngredient)
					{
						accepted = false;
						break;
					}
				}

				if(accepted)
					acceptedRecipes.put(craftingRecipe.value(), acceptedSlots);
			}

			if(acceptedRecipes.isEmpty())
				return;

			CraftingRecipe selected = acceptedRecipes.keySet().stream().toList().get(player.getRandom().nextInt(acceptedRecipes.size()));
			NonNullList<Integer> selectedList = acceptedRecipes.get(selected);

			ItemStack resultItem = selected.getResultItem(registryAccess).copy();

			for(Holder<Property> property : InfusedPropertiesHelper.getInfusedProperties(stack))
				InfusedPropertiesHelper.addProperty(resultItem, property);

			if(assimilate || player.getInventory().add(resultItem))
			{
				if(assimilate)
					stack.setDamageValue(0);
				for(int slotToShrink = 0; slotToShrink < player.getInventory().items.size(); slotToShrink++)
				{
					if(selectedList.get(slotToShrink) > 0 && !AlchemancyProperties.HOLLOW.get().shrinkContents(player.getInventory().getItem(slotToShrink), selectedList.get(slotToShrink)))
						player.getInventory().removeItem(slotToShrink, selectedList.get(slotToShrink));
				}
			}
		}
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipesMatchingOutput(ItemStack output, Level level)
	{
		return level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
				.filter(craftingRecipe ->
						ItemStack.isSameItem(craftingRecipe.value().getResultItem(level.registryAccess()), output) && !craftingRecipe.value().getIngredients().isEmpty()).toList();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x2BE9FF;
	}
}
