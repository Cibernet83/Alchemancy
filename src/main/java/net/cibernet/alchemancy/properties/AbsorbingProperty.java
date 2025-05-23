package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

public class AbsorbingProperty extends Property
{
	@Override
	public void onStackedOverMe(ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(shouldRepair(stack) && stack.getItem().isValidRepairItem(stack, otherStack))
		{
			repairItem(stack, stack.getMaxDamage() / 4);
			otherStack.shrink(1);
			event.setCanceled(true);
		}
	}

	@Override
	public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, ItemEntityPickupEvent.Pre event)
	{
		itemToPickUp.setNoPickUpDelay();
	}

	public static boolean scanInventoryAndConsume(ItemStack stack, Player player, Predicate<ItemStack> predicate, Consumer<ItemStack> function)
	{
		for (int i = 0; i < player.getInventory().getContainerSize(); i++)
		{
			ItemStack otherStack = player.getInventory().getItem(i);
			ItemStack repairStack = otherStack;

			ItemStack storedStack = AlchemancyProperties.HOLLOW.get().getData(repairStack);
			if(!storedStack.isEmpty())
				repairStack = storedStack;

			if(stack != repairStack && predicate.test(repairStack))
			{
				function.accept(repairStack);

				if(storedStack == repairStack)
					AlchemancyProperties.HOLLOW.get().setData(otherStack, storedStack);
				return true;
			}
		}

		return false;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(user instanceof Player player)
		{
			if(shouldRepair(stack))
				scanInventoryAndConsume(stack, player, consumeStack -> stack.getItem().isValidRepairItem(stack, consumeStack), consumeStack -> {

					repairItem(stack, stack.getMaxDamage() / 4);
					consumeStack.shrink(1);
				});
			else if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.CLAY_MOLD))
				scanInventoryAndConsume(stack, player, consumeStack -> consumeStack.is(AlchemancyTags.Items.REPAIRS_UNSHAPED_CLAY), consumeStack -> {

					ItemStack storedItem = ClayMoldProperty.repair(AlchemancyProperties.CLAY_MOLD.get().getData(stack));

					if(player.getItemBySlot(slot) == stack && stack.getCount() <= 1)
						player.setItemSlot(slot, storedItem);
					else if(!player.addItem(storedItem))
						player.drop(storedItem, true);

					ClayMoldProperty.playRepairEffects(player);
					stack.shrink(1);
					consumeStack.shrink(1);
				});
		}

		if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BUCKETING) && AlchemancyProperties.BUCKETING.get().isEmpty(stack))
		{
			Level level = user.level();

			for(int i = 0; i <= 1; i++)
			{
				BlockPos hitPos = user.blockPosition().above(i);
				BlockState hitState = level.getBlockState(hitPos);
				if (hitState.getBlock() instanceof BucketPickup bucketPickup && bucketPickup.pickupBlock(user instanceof Player player ? player : null, level, hitPos, hitState).getItem() instanceof BucketItem bucketItem) {
					bucketPickup.getPickupSound(hitState).ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
					level.gameEvent(user, GameEvent.FLUID_PICKUP, hitPos);

					AlchemancyProperties.BUCKETING.get().setData(stack, bucketItem.content);
					return;
				}
			}
		}
	}

	public static boolean shouldRepair(ItemStack stack)
	{
		return stack.isRepairable() && stack.getDamageValue() >= stack.getMaxDamage() / 4;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xEAE85D;
	}
}
