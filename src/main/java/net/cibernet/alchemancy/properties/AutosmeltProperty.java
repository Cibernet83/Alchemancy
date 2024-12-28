package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Collection;
import java.util.List;

public class AutosmeltProperty extends Property implements IDataHolder<Integer>
{

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event) {
		if(clickAction == ClickAction.SECONDARY && getData(stackedOnItem) <= 0)
		{
			int fuel = carriedItem.getBurnTime(RecipeType.SMELTING) / 200;

			if(fuel > 0)
			{
				if(carriedItem.hasCraftingRemainingItem())
				{
					ItemStack remainder = carriedItem.getCraftingRemainingItem();
					if(carriedItem.getCount() > 1)
					{
						player.drop(remainder, true);
						carriedItem.shrink(1);
					}
					else event.getCarriedSlotAccess().set(remainder.copy());
				}
				else carriedItem.shrink(1);

				setData(stackedOnItem, fuel);
				playRefuelSound(player);
				event.setCanceled(true);
			}
		}
	}


	@Override
	public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event)
	{
		if (checkAndConsumeFuel(breaker, tool))
		{
			Level level = breaker.level();
			for (ItemEntity drop : drops) {
				ItemStack stack = drop.getItem();
				RecipeHolder<? extends AbstractCookingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).orElse(null);
				if (recipe == null)
					recipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(stack), level).orElse(null);

				if (recipe != null) {
					ItemStack result = recipe.value().getResultItem(level.registryAccess()).copy();
					result.setCount(result.getCount() * stack.getCount());
					drop.setItem(result);
				}
			}
		}
	}


	@Override
	public void modifyLivingDrops(LivingEntity dropsSource, ItemStack weapon, LivingEntity user, Collection<ItemEntity> drops, LivingDropsEvent event)
	{
		if(checkAndConsumeFuel(dropsSource, weapon))
		{
			Level level = dropsSource.level();
			for (ItemEntity itemEntity : drops) {
				RecipeHolder<? extends AbstractCookingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemEntity.getItem()), level).orElse(null);
				if (recipe == null)
					recipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(itemEntity.getItem()), level).orElse(null);

				if (recipe != null) {
					ItemStack result = recipe.value().getResultItem(level.registryAccess()).copy();
					result.setCount(result.getCount() * itemEntity.getItem().getCount());
					itemEntity.setItem(result);
				}
			}
		}
	}

	public void playRefuelSound(Entity entity)
	{
		entity.playSound(AlchemancySoundEvents.SMELTING_RECHARGE.value());
	}

	public void playOutOfFuelSound(Entity entity)
	{
		entity.playSound(AlchemancySoundEvents.SMELTING_DEPLETED.value());
	}

	public boolean checkAndConsumeFuel(Entity entity, ItemStack stack)
	{
		int fuel = getData(stack);
		ItemStack storedStack = AlchemancyProperties.HOLLOW.get().getData(stack);

		if(!storedStack.isEmpty())
		{
			int refillFuel = storedStack.getBurnTime(RecipeType.SMELTING) / 200;

			if(fuel <= 1 && refillFuel > 0)
			{
				if(storedStack.hasCraftingRemainingItem())
				{
					ItemStack remainder = storedStack.getCraftingRemainingItem();
					if(storedStack.getCount() > 1)
					{
						HollowProperty.drop(entity, remainder.copy(), false, true);
						storedStack.shrink(1);
					}
					else storedStack = remainder.copy();
				}
				else storedStack.shrink(1);

				AlchemancyProperties.HOLLOW.get().setData(stack, storedStack);
				fuel += refillFuel;
				playRefuelSound(entity);
			}
		}

		if(fuel > 0)
		{
			setData(stack, fuel - 1);
			if(fuel == 1)
				playOutOfFuelSound(entity);

			return true;
		}
		return false;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF5B0F;
	}


	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		int fuel = getData(stack);

		return Component.translatable("property.detail", name.copy().withStyle(ChatFormatting.BOLD), fuel > 0 ?
				Component.translatable("property.detail.uses_left", fuel) :
				Component.translatable("property.detail.needs_refueling")
		).withColor(getColor(stack));
	}

	@Override
	public Integer readData(CompoundTag tag) {
		return tag.getInt("fuel");
	}

	@Override
	public CompoundTag writeData(Integer data) {
		return new CompoundTag(){{putInt("fuel", data);}};
	}

	@Override
	public Integer getDefaultData() {
		return 32;
	}
}
