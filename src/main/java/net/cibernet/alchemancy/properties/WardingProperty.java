package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class WardingProperty extends Property
{
	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if(slot.isArmor() || user.getUseItem() == weapon)
			event.setNewDamage(event.getNewDamage() * 0.85f);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x976997;
	}
}
