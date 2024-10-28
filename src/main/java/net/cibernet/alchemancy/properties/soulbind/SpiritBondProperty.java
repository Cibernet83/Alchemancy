package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;


public class SpiritBondProperty extends Property
{
	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if(weapon.isDamageableItem())
		{
			weapon.hurtAndBreak((int) event.getNewDamage(), user, slot);
			event.setNewDamage(event.getNewDamage() - 0.5f);
		}
	}

	@Override
	public void onHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, float amount)
	{
		if(stack.isDamaged())
			stack.setDamageValue(stack.getDamageValue() - (int)amount);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF9129;
	}
}
