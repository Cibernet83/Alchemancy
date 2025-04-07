package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

public class RemoveInfusionsProperty extends Property
{
	@Override
	public void onStackedOverItem(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction == ClickAction.SECONDARY && !stackedOnItem.isEmpty())
		{
			InfusedPropertiesHelper.clearAllInfusions(stackedOnItem);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x566E7F;
	}
}
