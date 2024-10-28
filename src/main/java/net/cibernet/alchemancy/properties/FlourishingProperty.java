package net.cibernet.alchemancy.properties;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

public class FlourishingProperty extends Property
{
	@Override
	public void modifyHeal(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingHealEvent event)
	{
		event.setAmount(Mth.ceil(event.getAmount() * 1.2f));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xF4429C;
	}
}
