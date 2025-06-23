package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.text.DecimalFormat;
import java.util.List;

public class BatteryPoweredProperty extends Property {

	private static final int COST = 100;
	public static final int CAPACITY = COST * 100;

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		repairItem(stack);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
		repairItem(root.getItem());
	}

	private void repairItem(ItemStack stack) {
		var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energy == null || !stack.isDamaged() || energy.extractEnergy(COST, true) != COST) return;

		energy.extractEnergy(COST, false);
		repairItem(stack, 1);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {

		var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if (energy == null)
			return super.getDisplayText(stack);
		DecimalFormat df = new DecimalFormat("###,###,###");
		return Component.translatable("property.detail", super.getDisplayText(stack),
						Component.translatable("property.detail.fe",
								df.format(energy.getEnergyStored()),
								df.format(energy.getMaxEnergyStored())))
				.withColor(getColor(stack));
	}

	@Override
	public int getColor(ItemStack stack) {

		return ColorUtils.interpolateColorsOverTime(0.25f, 0xB53000, 0xB53000, 0xD87C0A);
	}
}
