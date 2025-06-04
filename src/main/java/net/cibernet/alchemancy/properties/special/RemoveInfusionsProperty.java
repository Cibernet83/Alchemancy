package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveInfusionsProperty extends Property {
	@Override
	public void onStackedOverItem(ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if (clickAction == ClickAction.SECONDARY && !stackedOnItem.isEmpty()) {
			InfusedPropertiesHelper.clearAllInfusions(stackedOnItem);
			isCancelled.set(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x566E7F;
	}
}
