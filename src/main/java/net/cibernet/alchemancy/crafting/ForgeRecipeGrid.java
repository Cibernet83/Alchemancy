package net.cibernet.alchemancy.crafting;

import com.mojang.datafixers.util.Pair;
import net.cibernet.alchemancy.blocks.AlchemancyCatalystBlock;
import net.cibernet.alchemancy.blocks.InfusionPedestalBlock;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.blocks.blockentities.IEssenceHolder;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ForgeRecipeGrid implements RecipeInput
{
	private final ArrayList<ItemStackHolderBlockEntity> items = new ArrayList<>();

	private final ArrayList<Object> slotOrder = new ArrayList<>();

	private final ItemStackHolderBlockEntity forge;
	private ItemStack currentOutput;

	private Optional<Boolean> performingTransmutation = Optional.empty();
	public Optional<Boolean> applyGlint = Optional.empty();
	private final ArrayList<Holder<Property>> warpResults = new ArrayList<>();



	public static ItemStack resolveInteractions(ItemStack input, Level level)
	{
		ItemStack gridInput = input.copy();
		gridInput.setCount(1);
		ForgeRecipeGrid grid = new ForgeRecipeGrid(gridInput);

		for(int i = 0; i < 128; i++)
		{
			Optional<RecipeHolder<AbstractForgeRecipe<?>>> recipe = OUT_OF_FORGE_INTERACTIONS_CHECK.getRecipeFor(grid, level);
			if(recipe.isPresent())
				grid.processRecipe(recipe.get().value(), level.registryAccess());
			else break;
		}

		ItemStack result = grid.getCurrentOutput();
		if(!result.isEmpty())
			result.setCount(result.getCount() * input.getCount());

		return InfusedPropertiesHelper.truncateProperties(result);
	}

	private static RecipeManager.CachedCheck<ForgeRecipeGrid, AbstractForgeRecipe<?>> OUT_OF_FORGE_INTERACTIONS_CHECK = (input, level) -> level.getRecipeManager().getRecipesFor(AlchemancyRecipeTypes.ALCHEMANCY_FORGE.get(), input, level).stream().filter(recipe -> recipe.value().matches(input, level) && !recipe.value().isTransmutation())
			.min(Comparator.comparingInt(recipe -> recipe.value().getRecipeCompareValue(input)));;

	@Override
	public ItemStack getItem(int index)
	{
		return items.size() <= index ? ItemStack.EMPTY : items.get(0).getItem(0);
	}

	private Pair<Integer, Integer>[] slotOffsets = new Pair[]
			{
					new Pair<>(1, 0), new Pair<>(1, 1), new Pair<>(0, 1), new Pair<>(-1, 1),
					new Pair<>(-1, 0), new Pair<>(-1, -1), new Pair<>(0, -1), new Pair<>(1, -1)
			};

	public ForgeRecipeGrid(ItemStack stack)
	{
		forge = new ItemStackHolderBlockEntity(BlockPos.ZERO, AlchemancyBlocks.ALCHEMANCY_FORGE.get().defaultBlockState());
		forge.setItem(stack);
		currentOutput = stack.copy();
	}

	public ForgeRecipeGrid(Level level, BlockPos pos, ItemStackHolderBlockEntity forge)
	{
		this.forge = forge;
		currentOutput = forge.getItem().copy();
		currentOutput.setCount(1);

		for(Pair<Integer, Integer> offset : slotOffsets)
		{
			Direction direction = forge.getBlockState().getValue(InfusionPedestalBlock.FACING);
			BlockPos lookupPos = pos.relative(direction, offset.getFirst()).relative(direction.getClockWise(), offset.getSecond());

			BlockEntity lookupBlockEntity = level.getBlockEntity(lookupPos);
			BlockState lookupState = level.getBlockState(lookupPos);

			if(lookupState.is(AlchemancyBlocks.INFUSION_PEDESTAL) && lookupBlockEntity instanceof ItemStackHolderBlockEntity pedestal)
			{
				items.add(pedestal);
				slotOrder.add(pedestal);
			}
		}
	}


	@Override
	public int size() {
		return items.size() + (forge.getItem().isEmpty() ? 0 : 1);
	}

	@Override
	public boolean isEmpty()
	{
		return forge.isEmpty() && areIngredientsEmpty();
	}

	public boolean areIngredientsEmpty()
	{
		for (ItemStackHolderBlockEntity pedestal : items) {
			if(!pedestal.isEmpty())
				return false;
		}

		return true;
	}

	public ItemStack getCurrentOutput() {
		return currentOutput;
	}

	public ItemStackHolderBlockEntity getForge()
	{
		return forge;
	}

	public int getSlot(Object o)
	{
		return slotOrder.indexOf(o);
	}

	public int getSlotFor(Ingredient infusable)
	{
		for (ItemStackHolderBlockEntity item : items) {
			if (infusable.test(item.getItem())) {
				return getSlot(item);
			}
		}

		return -1;
	}

	private HashMap<AbstractForgeRecipe<?>, Integer> CACHED_COMPARE_VALUES = new HashMap<>();

	public int getRecipeCompareValue(AbstractForgeRecipe<?> recipe, List<Ingredient> infusables, List<Holder<Property>> properties, int priority)
	{
//		if(CACHED_COMPARE_VALUES.containsKey(recipe))
//			return CACHED_COMPARE_VALUES.get(recipe);

		int slots = 0;
		int slotValue = 0;

		//
		if(!infusables.isEmpty())
		{
			ArrayList<ItemStackHolderBlockEntity> items = new ArrayList<>(this.items);

			for (Ingredient infusable : infusables)
			{
				int i = 0;
				for (ItemStackHolderBlockEntity item : items)
				{
					if(infusable.test(item.getItem()))
					{
						slots++;
						items.remove(i);
						slotValue += getSlot(item);
						break;
					}
					i++;
				}
			}
		}

		//

		int infusionValue = 0;
		if(!properties.isEmpty())
		{
			var infusions = InfusedPropertiesHelper.getInfusedProperties(getCurrentOutput()).stream().sorted(Comparator.comparingInt(p -> p.value().getPriority())).toList();

			for (int i = 0; i < infusions.size(); i++) {
				var infusion = infusions.get(i);

				infusionValue += ((properties.contains(infusion)) ? 0 : i + 1);
			}
		}


		int result = ((priority - AbstractForgeRecipe.MIN_PRIORITY) << 9) + ((8-slots) << 6) + Mth.clamp(slotValue, 0, 36) + infusionValue;

		//System.out.println(recipe + " slotValue: " + slotValue + " slots: " + slots + " priority: " + priority + " result: " + result);

		CACHED_COMPARE_VALUES.put(recipe, result);
		return result;
	}

	public ArrayList<ItemStackHolderBlockEntity> getItemPedestals()
	{
		return items;
	}

	public boolean consumeItem(ItemStackHolderBlockEntity pedestal)
	{
		if(!slotOrder.contains(pedestal))
			return false;
		ItemStack stack = pedestal.getItem();
		if(stack.hasFoil())
			applyGlint = Optional.of(true);
		else if(stack.is(AlchemancyTags.Items.INFUSION_REMOVES_GLINT))
			applyGlint = Optional.of(false);

		pedestal.removeItem(1);
		markAsProcessed(pedestal);

		cachedDormantProperties = null;

		return true;
	}

	public void markAsProcessed(ItemStackHolderBlockEntity pedestal) {
		slotOrder.remove(pedestal);
		items.remove(pedestal);
	}

	public boolean testInfusables(List<Ingredient> infusables, boolean consume)
	{
		if(infusables.isEmpty())
			return true;

		ArrayList<ItemStackHolderBlockEntity> items = consume ? this.items : new ArrayList<>(this.items);

		for (Ingredient infusable : infusables)
		{
			int i = 0;
			for (ItemStackHolderBlockEntity item : items)
			{
				ItemStack stack = item.getItem();
				if(infusable.test(stack))
				{
					if(consume)
						consumeItem(item);
					break;
				}
				i++;
			}
			if(i >= items.size())
				return false;
		}

		return true;
	}

	public boolean testProperties(List<Holder<Property>> propertiesToTest, boolean consume)
	{
		if(propertiesToTest.isEmpty())
			return true;
		if(!currentOutput.has(AlchemancyItems.Components.INFUSED_PROPERTIES))
			return false;
		InfusedPropertiesComponent properties = currentOutput.get(AlchemancyItems.Components.INFUSED_PROPERTIES);

		for (Holder<Property> propertyHolder : propertiesToTest) {
			if(!properties.hasProperty(propertyHolder))
			{
				return false;
			} else if(consume)
				InfusedPropertiesHelper.removeProperty(currentOutput, propertyHolder);
		}

		return true;
	}

	public void processRecipe(AbstractForgeRecipe<?> recipe, RegistryAccess registryAccess)
	{
		currentOutput = recipe.assemble(this, registryAccess);
		performingTransmutation = Optional.of(recipe.isTransmutation());
	}

	public Optional<Boolean> getPerformingTransmutation()
	{
		return performingTransmutation;
	}

	public boolean canPerformTransmutation()
	{
		return performingTransmutation.isEmpty() || performingTransmutation.get();
	}

	public boolean isPerformingTransmutation()
	{
		return performingTransmutation.isPresent() && performingTransmutation.get();
	}

	public boolean handleDormantRecipes(boolean consume)
	{
		return handleDormantRecipes(currentOutput, consume);
	}
	public boolean handleDormantRecipes(ItemStack currentOutput, boolean consume)
	{
		if(forge.getItem().is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS))
			return false;

		boolean success = false;
		for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(items))
		{
			ItemStack stack = pedestal.getItem();
			ItemStack target = !consume ? currentOutput.copy() : currentOutput;

			List<Holder<Property>> properties = AlchemancyProperties.getDormantProperties(stack);

			properties.addAll(stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties());

			if(properties.isEmpty())
				continue;

			boolean perform = false;
			AtomicBoolean consumeItem = new AtomicBoolean(true);
			for (Holder<Property> property : List.copyOf(properties))
			{
				if(property.value().onInfusedByDormantProperty(target, stack, this, properties, consumeItem))
					perform = true;
			}

			if(perform)
			{
				if(consume)
				{
					InfusedPropertiesHelper.addProperties(target, properties);
					if(consumeItem.get())
					{
						items.remove(pedestal);
						consumeItem(pedestal);
					}
				}

				success = true;
				break;
			}
		}



		return success;
	}

	private List<Holder<Property>> cachedDormantProperties = null;
	public List<Holder<Property>> getDormantProperties()
	{
		if(cachedDormantProperties != null)
			return cachedDormantProperties;

		final List<Holder<Property>> result = new ArrayList<>();

		for (ItemStackHolderBlockEntity pedestal : new ArrayList<>(items))
		{
			ItemStack stack = pedestal.getItem();
			ItemStack target = currentOutput.copy();

			if(stack.is(AlchemancyTags.Items.REMOVES_INFUSIONS))
			{
				result.clear();
				continue;
			}

			List<Holder<Property>> properties = AlchemancyProperties.getDormantProperties(stack);

			properties.addAll(stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties());

			if(properties.isEmpty())
				continue;

			boolean perform = false;
			for (Holder<Property> property : properties) {
				if(property.value().onInfusedByDormantProperty(target, stack, this, properties, new AtomicBoolean(false)))
					perform = true;
			}

			if(perform)
				result.addAll(properties);
		}
		cachedDormantProperties = result;
		return result;
	}

	public boolean hasBeenWarped(List<Holder<Property>> properties)
	{
		for (Holder<Property> property : properties) {
			if(!warpResults.contains(property))
				return false;
		}
		return true;
	}

	public void consumeWarped(List<Holder<Property>> properties) {
		warpResults.addAll(properties);
	}

	public boolean shouldConsumeWarped() {
		return !warpResults.isEmpty();
	}
}
