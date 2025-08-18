package net.cibernet.alchemancy.properties.entangled;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class InteractEntangledProperty extends AbstractEntangledProperty {
	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.isCanceled() && !getData(event.getItemStack()).equals(getDefaultData())) {
			EquipmentSlot slot = event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			if (event.getItemStack() == event.getEntity().getItemBySlot(slot)) {
				event.getEntity().setItemSlot(slot, shift(event.getItemStack(), event.getEntity()));
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if (!isCancelled.get() && clickAction == ClickAction.SECONDARY && carriedItem.isEmpty() && stack == stackedOnSlot.getItem()) {
			stackedOnSlot.set(shift(stack, player));
			isCancelled.set(true);
		} else super.onStackedOverMe(carriedItem, stack, player, clickAction, carriedSlot, stackedOnSlot, isCancelled);
	}
}
