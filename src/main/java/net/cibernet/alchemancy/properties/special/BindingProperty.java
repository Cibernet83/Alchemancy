package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class BindingProperty extends Property {
	@Override
	public void onStackedOverItem(ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if (clickAction == ClickAction.SECONDARY && !stackedOnItem.isEmpty())
			isCancelled.set(true);
	}

	public static void toggleBind(ItemStack target) {
		if (InfusedPropertiesHelper.hasProperty(target, AlchemancyProperties.UNMOVABLE))
			InfusedPropertiesHelper.removeProperty(target, AlchemancyProperties.UNMOVABLE);
		else InfusedPropertiesHelper.addProperty(target, AlchemancyProperties.UNMOVABLE);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC1C100;
	}
}
