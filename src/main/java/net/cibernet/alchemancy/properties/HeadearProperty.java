package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HeadearProperty extends Property
{
	@Override
	public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
		return EquipmentSlot.HEAD;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE24F74;
	}
}
