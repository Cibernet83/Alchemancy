package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
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

public class AbsorbingProperty extends Property
{
	@Override
	public void onStackedOverMe(ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(shouldRepair(stack) && stack.getItem().isValidRepairItem(stack, otherStack))
		{
			stack.setDamageValue(stack.getDamageValue() - Math.min(stack.getDamageValue(), stack.getMaxDamage() / 4));
			otherStack.shrink(1);
			event.setCanceled(true);
		}
	}

	@Override
	public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, ItemEntityPickupEvent.Pre event)
	{
		itemToPickUp.setNoPickUpDelay();
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(shouldRepair(stack) && user instanceof Player player)
		{
			for (int i = 0; i < player.getInventory().items.size(); i++)
			{
				ItemStack otherStack = player.getInventory().items.get(i);
				ItemStack repairStack = player.getInventory().items.get(i);

				ItemStack storedStack = AlchemancyProperties.HOLLOW.get().getData(repairStack);
				if(!storedStack.isEmpty())
					repairStack = storedStack;

				if(stack != repairStack && stack.getItem().isValidRepairItem(stack, repairStack))
				{
					stack.setDamageValue(stack.getDamageValue() - Math.min(stack.getDamageValue(), stack.getMaxDamage() / 4));
					repairStack.shrink(1);

					if(storedStack == repairStack)
						AlchemancyProperties.HOLLOW.get().setData(otherStack, storedStack);
					return;
				}
			}
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
