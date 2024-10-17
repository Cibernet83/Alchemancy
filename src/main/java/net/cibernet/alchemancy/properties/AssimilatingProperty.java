package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

public class AssimilatingProperty extends Property
{
	@Override
	public void onStackedOverMe(ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(stack.isDamaged() && stack != otherStack && ItemStack.isSameItem(stack, otherStack))
		{
			if(!otherStack.isDamaged())
				stack.setDamageValue(0);
			else stack.setDamageValue(Math.max(0, stack.getDamageValue() - (stack.getMaxDamage() - otherStack.getDamageValue())));
			otherStack.shrink(1);
			event.setCanceled(true);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(stack.isDamageableItem() && user instanceof Player player)
		{
			for (int i = 0; i < player.getInventory().items.size(); i++)
			{
				ItemStack otherStack = player.getInventory().items.get(i);
				if(stack != otherStack && ItemStack.isSameItem(stack, otherStack) && (shouldRepair(stack) || stack.getDamageValue() >= stack.getMaxDamage() - otherStack.getDamageValue()))
				{
					stack.setDamageValue(Math.max(0, stack.getDamageValue() - (stack.getMaxDamage() - otherStack.getDamageValue())));
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
