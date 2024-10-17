package net.cibernet.alchemancy.properties;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

public class WealthyProperty extends Property
{
	@Override
	public void modifyEnchantmentLevels(GetEnchantmentLevelEvent event)
	{
		ItemEnchantments.Mutable enchantments = event.getEnchantments();
		modifyEnchantmentLevel(enchantments, event.getLookup(), Enchantments.FORTUNE, level -> level+1);
		modifyEnchantmentLevel(enchantments, event.getLookup(), Enchantments.LOOTING, level -> level+1);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x1CFF6F;
	}
}
