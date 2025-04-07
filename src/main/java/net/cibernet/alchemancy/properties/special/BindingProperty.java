package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

public class BindingProperty extends Property
{
	@Override
	public void onStackedOverItem(ItemStack carriedItem, ItemStack stackedOverItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction == ClickAction.SECONDARY && !stackedOverItem.isEmpty())
			event.setCanceled(true);
	}

	public static void toggleBind(ItemStack target)
	{
		if(InfusedPropertiesHelper.hasProperty(target, AlchemancyProperties.UNMOVABLE))
			InfusedPropertiesHelper.removeProperty(target, AlchemancyProperties.UNMOVABLE);
		else InfusedPropertiesHelper.addProperty(target, AlchemancyProperties.UNMOVABLE);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC1C100;
	}
}
