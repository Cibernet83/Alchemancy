package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class NonlethalProperty extends Property
{
	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {
		event.setCanceled(true);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFFFFFF;
	}

	@Override
	public int getPriority() {
		return Priority.HIGHER;
	}
}
