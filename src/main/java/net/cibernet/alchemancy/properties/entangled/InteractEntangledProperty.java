package net.cibernet.alchemancy.properties.entangled;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class InteractEntangledProperty extends AbstractEntangledProperty
{
	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled() && !getData(event.getItemStack()).equals(getDefaultData()))
		{
			EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			if(event.getItemStack() == event.getEntity().getItemBySlot(slot))
			{
				event.getEntity().setItemSlot(slot, shift(event.getItemStack()));
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction == ClickAction.SECONDARY && carriedItem.isEmpty() && stackedOnItem == event.getSlot().getItem())
		{
			event.getSlot().set(shift(stackedOnItem));
			event.setCanceled(true);
		}
		else super.onStackedOverMe(carriedItem, stackedOnItem, player, clickAction, event);
	}
}
