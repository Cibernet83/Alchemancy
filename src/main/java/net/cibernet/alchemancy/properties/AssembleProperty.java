package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class AssembleProperty extends Property
{
	private static final List<DataComponentType<?>> COMPONENTS_TO_CLONE = List.of(
			AlchemancyItems.Components.INFUSED_PROPERTIES.get(),
			AlchemancyItems.Components.PROPERTY_DATA.get(),
			DataComponents.CUSTOM_NAME,
			DataComponents.LORE,
			DataComponents.ITEM_NAME,
			DataComponents.PROFILE,
			DataComponents.ENCHANTMENT_GLINT_OVERRIDE,
			DataComponents.BANNER_PATTERNS,
			DataComponents.DYED_COLOR,
			DataComponents.CAN_BREAK,
			DataComponents.CAN_PLACE_ON
	);

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
				NonNullList<Integer> acceptedSlots = NonNullList.withSize(player.getInventory().getContainerSize(), 0);

				if(!(assimilate || craftingRecipe.value().getResultItem(registryAccess).getCount() + stack.getCount() <= stack.getMaxStackSize()))
					return;

				boolean accepted = true;
				for(Ingredient ingredient : craftingRecipe.value().getIngredients())
				{
					if(ingredient.isEmpty())
						continue;

					boolean hasIngredient = false;
					for (int i = 0; i < acceptedSlots.size(); i++)
					{
						ItemStack inventoryStack = player.getInventory().getItem(i);
						ItemStack storedItem = AlchemancyProperties.HOLLOW.get().getData(inventoryStack);
						if(!storedItem.isEmpty())
							inventoryStack = storedItem;

						if(stack == inventoryStack || InfusedPropertiesHelper.hasProperty(inventoryStack, AlchemancyProperties.ASSEMBLING))
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

			for (DataComponentType<?> dataComponentType : COMPONENTS_TO_CLONE) {
				copyComponentTo(dataComponentType, stack, resultItem);
			}

			if(assimilate || player.getInventory().add(resultItem))
			{
				if(assimilate)
					stack.setDamageValue(0);
				for(int slotToShrink = 0; slotToShrink < player.getInventory().getContainerSize(); slotToShrink++)
				{
					if(selectedList.get(slotToShrink) > 0 && !AlchemancyProperties.HOLLOW.get().shrinkContents(player.getInventory().getItem(slotToShrink), selectedList.get(slotToShrink)))
						player.getInventory().removeItem(slotToShrink, selectedList.get(slotToShrink));
				}
			}
		}
	}

	public static <T> void copyComponentTo(DataComponentType<T> componentType, ItemStack from, ItemStack to)
	{
		if(from.has(componentType))
			to.set(componentType, from.get(componentType));
	}

	@Override
	public @Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		Level level = player.level();
		ItemStack stack = root.getItem();
		List<RecipeHolder<CraftingRecipe>> availableRecipes = getRecipesMatchingOutput(stack, level);

		Tuple<CraftingRecipe, NonNullList<Integer>> selectedRecipe = null;
		for (RecipeHolder<CraftingRecipe> craftingRecipe : availableRecipes)
		{
			NonNullList<Integer> acceptedSlots = NonNullList.withSize(player.getInventory().items.size(), 0);

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

					if(stack == inventoryStack || InfusedPropertiesHelper.hasProperty(inventoryStack, AlchemancyProperties.ASSEMBLING))
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
				selectedRecipe = new Tuple<>(craftingRecipe.value(), acceptedSlots);
		}

		if(selectedRecipe != null && player.addItem(selectedRecipe.getA().getResultItem(level.registryAccess()).copy()))
		{
			NonNullList<Integer> selectedList = selectedRecipe.getB();
			if(!level.isClientSide())
				for(int slotToShrink = 0; slotToShrink < player.getInventory().items.size(); slotToShrink++)
				{
					if(selectedList.get(slotToShrink) > 0 && !AlchemancyProperties.HOLLOW.get().shrinkContents(player.getInventory().getItem(slotToShrink), selectedList.get(slotToShrink)))
						player.getInventory().removeItem(slotToShrink, selectedList.get(slotToShrink));
				}
			return ItemInteractionResult.sidedSuccess(level.isClientSide());
		}

		return super.onRootedRightClick(root, player, hand, hitResult);
	}

	public static List<RecipeHolder<CraftingRecipe>> getRecipesMatchingOutput(ItemStack output, Level level)
	{
		return level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
				.filter(craftingRecipe ->
						ItemStack.isSameItem(craftingRecipe.value().getResultItem(level.registryAccess()), output) && !craftingRecipe.value().getIngredients().isEmpty()).toList();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xA066C4;
	}
}
