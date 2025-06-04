package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class AssimilatingProperty extends Property
{
	@Override
	public void onStackedOverMe(ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled)
	{
		if(stack.isDamaged() && stack != otherStack && ItemStack.isSameItem(stack, otherStack))
		{
			repairItem(stack, (stack.getMaxDamage() - otherStack.getDamageValue()));
			otherStack.shrink(1);
			isCancelled.set(true);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(stack.isDamageableItem() && user instanceof Player player)
		{
			for (int i = 0; i < player.getInventory().getContainerSize(); i++)
			{
				ItemStack otherStack = player.getInventory().getItem(i);
				if(stack != otherStack && ItemStack.isSameItem(stack, otherStack) && (shouldRepair(stack) || stack.getDamageValue() >= stack.getMaxDamage() - otherStack.getDamageValue()))
				{
					repairItem(stack, (stack.getMaxDamage() - otherStack.getDamageValue()));
					player.getInventory().removeItem(i, 1);
					return;
				}
			}
		}
	}

	public static boolean shouldRepair(ItemStack stack)
	{
		return stack.getDamageValue() >= stack.getMaxDamage() - 5;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x62F767;
	}
}
