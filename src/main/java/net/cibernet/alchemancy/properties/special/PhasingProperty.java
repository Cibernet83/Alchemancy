package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class PhasingProperty extends Property
{
	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		itemEntity.noPhysics = true;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if(slot.isArmor())
			user.noPhysics = true;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0;
	}
}
