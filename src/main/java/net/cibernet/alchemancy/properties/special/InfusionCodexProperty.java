package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.client.screen.InfusionCodexEntryScreen;
import net.cibernet.alchemancy.client.screen.InfusionCodexIndexScreen;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class InfusionCodexProperty extends Property {

	public static Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> inspectItem(Player user, ItemStack stack) {

		Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> result = new HashMap<>();

		InfusedPropertiesHelper.getInnateProperties(stack).forEach(propertyHolder -> addEntry(result, propertyHolder));
		InfusedPropertiesHelper.getInfusedProperties(stack).forEach(propertyHolder -> addEntry(result, propertyHolder));
		InfusedPropertiesHelper.getStoredProperties(stack).forEach(propertyHolder -> addEntry(result, propertyHolder));

		if(InfusedPropertiesHelper.hasProperty(user.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING) ||
				InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.REVEALED) ||
				InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AWAKENED))
			AlchemancyProperties.getDormantProperties(stack).forEach(propertyHolder -> addEntry(result, propertyHolder));

		return result;
	}

	public static boolean canInspect(Player user, ItemStack stack) {

		if(user.level().isClientSide())
			return !inspectItem(user, stack).isEmpty();

		if(InfusedPropertiesHelper.hasProperty(user.getItemBySlot(EquipmentSlot.HEAD), AlchemancyProperties.REVEALING) && !AlchemancyProperties.getDormantProperties(stack).isEmpty())
			return true;

		return !InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty() || !InfusedPropertiesHelper.getInnateProperties(stack).isEmpty() || !InfusedPropertiesHelper.getStoredProperties(stack).isEmpty();
	}

	private static void addEntry(Map<Holder<Property>, CodexEntryReloadListenener.CodexEntry> map, Holder<Property> propertyHolder) {
		if(!map.containsKey(propertyHolder) && CodexEntryReloadListenener.getEntries().containsKey(propertyHolder))
			map.put(propertyHolder, CodexEntryReloadListenener.getEntries().get(propertyHolder));
	}

	@Override
	public void onStackedOverItem(ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {

		if(clickAction != ClickAction.SECONDARY || stackedOnItem.isEmpty() || ItemStack.isSameItemSameComponents(stackedOnItem, stack)) return;

		if(player.level().isClientSide() && canInspect(player, stackedOnItem))
			Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(stackedOnItem));
		isCancelled.set(true);
	}

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if(clickAction != ClickAction.SECONDARY || carriedItem.isEmpty() || ItemStack.isSameItemSameComponents(stackedOnItem, carriedItem)) return;

		if(player.level().isClientSide() && canInspect(player, carriedItem))
			Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(carriedItem));
		isCancelled.set(true);
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.isCanceled()) {
			if (event.getLevel().isClientSide())
				Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(event.getItemStack().getDisplayName()));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAD101C;
	}
}
