package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentedProperty extends Property {

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if(!carriedItem.isEmpty() || clickAction != ClickAction.SECONDARY) return;

		if(stack.getMaxDamage() <= 1)
		{
			consumeItem(player, stack, EquipmentSlot.MAINHAND);
			isCancelled.set(true);
		}
		else {
			InfusedPropertiesHelper.removeProperty(stack, asHolder());
			stack.set(DataComponents.MAX_DAMAGE, Math.floorDiv(stack.getMaxDamage(), 2));
			stack.set(DataComponents.DAMAGE, Math.floorDiv(stack.getDamageValue(), 2));
			if(AlchemancyProperties.RESIZED.get().hasData(stack))
				AlchemancyProperties.RESIZED.get().setData(stack, Math.max(0.25f, AlchemancyProperties.RESIZED.get().getData(stack) * 0.5f));


			ItemStack newStack = stack.copy();
			newStack.setCount(1);
			carriedSlot.set(newStack);
			isCancelled.set(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		//Split color like magnetic
		return ColorUtils.flashColorsOverTime(1000,0xC7ABD8, 0x5D3B72);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return getName(stack);
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(getLanguageKey()+".format",
				Component.translatable(getLanguageKey()+".a").withColor(0xC7ABD8),
				Component.translatable(getLanguageKey()+".b").withStyle(ChatFormatting.ITALIC).withColor(0x5D3B72));
	}
}
