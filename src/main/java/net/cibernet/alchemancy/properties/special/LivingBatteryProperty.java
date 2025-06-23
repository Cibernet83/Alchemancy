package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

public class LivingBatteryProperty extends Property {

	public static final int CONVERSION = 50;

	@Override
	public int onItemRepaired(ItemStack stack, int amount, int original) {

		int toCharge = amount - stack.getDamageValue();
		if (toCharge <= 0) return amount;

		var cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (cap == null || !cap.canReceive()) return amount;

		return Math.max(0, amount - (cap.receiveEnergy(toCharge * CONVERSION, false)) / CONVERSION);
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.25f, 0xB4EF34, 0xB4EF34, 0xE2FC79);
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}
}
