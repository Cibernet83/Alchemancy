package net.cibernet.alchemancy.crafting;

import com.mojang.datafixers.util.Pair;
import net.cibernet.alchemancy.blocks.InfusionPedestalBlock;
import net.cibernet.alchemancy.blocks.blockentities.EssenceContainer;
import net.cibernet.alchemancy.blocks.blockentities.IEssenceHolder;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class ForgeRecipeGrid implements RecipeInput
{
	private final ArrayList<ItemStackHolderBlockEntity> items = new ArrayList<>();
	private final ArrayList<EssenceContainer> essences = new ArrayList<>();

	private final ArrayList<Object> slotOrder = new ArrayList<>();

	private final ItemStackHolderBlockEntity forge;
	private ItemStack currentOutput;

	private Optional<Boolean> performingTransmutation = Optional.empty();
	public Optional<Boolean> applyGlint = Optional.empty();
	private final ArrayList<Holder<Property>> warpResults = new ArrayList<>();

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
			else if(lookupState.is(AlchemancyBlocks.ESSENCE_INJECTOR) && lookupBlockEntity instanceof IEssenceHolder essenceHolder)
			{
				EssenceContainer container = essenceHolder.getEssenceContainer();
				essences.add(container);
				slotOrder.add(container);
			}
		}
	}


	@Override
	public int size() {
		return items.size() + essences.size() + (forge.getItem().isEmpty() ? 0 : 1);
	}

	@Override
	public boolean isEmpty()
	{
		return forge.isEmpty() && areIngredientsEmpty();
	}

	public boolean areIngredientsEmpty()
	{
		for (EssenceContainer essence : essences)
			if(!essence.isEmpty())
				return false;
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

	public int getSlotFor(EssenceContainer essenceContainer)
	{
		EssenceContainer testEssence = new EssenceContainer(essenceContainer.getEssence(), essenceContainer.getAmount(), 0);

		for (EssenceContainer essence : essences) {
			if (essence.transferTo(testEssence, testEssence.getLimit(), false, false) > 0) {

			}
		}

		return -1;
	}

	private HashMap<AbstractForgeRecipe<?>, Integer> CACHED_COMPARE_VALUES = new HashMap<>();

	public int getRecipeCompareValue(AbstractForgeRecipe<?> recipe, List<Ingredient> infusables, List<EssenceContainer> essencesToTest, int priority)
	{
		if(CACHED_COMPARE_VALUES.containsKey(recipe))
			return CACHED_COMPARE_VALUES.get(recipe);

		int slots = 0;
		int slotValue = 0;

		//
		if(!essencesToTest.isEmpty()) {
			ArrayList<EssenceContainer> essences = new ArrayList<>(this.essences);

			for (EssenceContainer e : essencesToTest) {
				EssenceContainer testEssence = new EssenceContainer(e.getEssence(), e.getAmount(), 0);

				ArrayList<EssenceContainer> ignoredEssences = new ArrayList<>();

				for (EssenceContainer essence : essences)
				{
					if(ignoredEssences.contains(essence))
						continue;

					if (essence.transferTo(testEssence, testEssence.getLimit(), false, false) > 0)
					{
						slots++;
						slotValue += getSlot(essence);

						if (essence.isEmpty())
							ignoredEssences.add(essence);

						if (testEssence.isFull())
							break;
					}
				}

			}
		}

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


		int result = ((priority - AbstractForgeRecipe.MIN_PRIORITY) << 9) + ((8-slots) << 6) + Mth.clamp(slotValue, 0, 36);

		//System.out.println(recipe + " slotValue: " + slotValue + " slots: " + slots + " priority: " + priority + " result: " + result);

		CACHED_COMPARE_VALUES.put(recipe, result);
		return result;
	}

	public ArrayList<ItemStackHolderBlockEntity> getItemPedestals()
	{
		return items;
	}

	public ArrayList<EssenceContainer> getEssenceContainers()
	{
		return essences;
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
		pedestal.notifyInventoryUpdate();
		slotOrder.remove(pedestal);
		return true;
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
			else items.remove(i);
		}

		return true;
	}

	public boolean testEssences(List<EssenceContainer> essencesToTest, boolean consume)
	{
		if(essencesToTest.isEmpty())
			return true;

		ArrayList<EssenceContainer> essences = new ArrayList<>(this.essences);

		for (EssenceContainer e : essencesToTest)
		{
			EssenceContainer testEssence = new EssenceContainer(e.getEssence(), e.getAmount(), 0);

			boolean successful = false;
			for (EssenceContainer essence : essences)
			{
				if(essence.transferTo(testEssence, testEssence.getLimit(), false, consume) > 0)
				{
					if(essence.isEmpty())
						this.essences.remove(essence);

					if(testEssence.isFull())
					{
						successful = true;
						break;
					}
				}
			}

			if(!successful)
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

			if(stack.is(AlchemancyTags.Items.REMOVES_INFUSIONS))
			{
				if(!InfusedPropertiesHelper.getInfusedProperties(target).isEmpty())
				{
					if(consume)
					{
						consumeItem(pedestal);
						InfusedPropertiesHelper.clearAllInfusions(target);
					}
					return true;
				}
				return false;
			}

			List<Holder<Property>> properties = AlchemancyProperties.getDormantProperties(stack);

			properties.addAll(stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties());

			if(properties.isEmpty())
				continue;

			boolean perform = false;
			for (Holder<Property> property : properties) {
				if(property.value().onInfusedByDormantProperty(target, stack, this))
					perform = true;
			}

			if(perform)
			{
				success = true;

				if(consume)
				{
					InfusedPropertiesHelper.addProperties(target, properties);
					consumeItem(pedestal);
				}
			}
		}



		return success;
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
