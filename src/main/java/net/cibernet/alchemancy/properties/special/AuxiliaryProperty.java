package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AuxiliaryProperty extends Property
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(user instanceof LivingEntity living)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEquippedTick(living, EquipmentSlot.MAINHAND, stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xCAE6E1;
	}
}
