package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ArcaneProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xEA23C2;
	}

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(!event.getSource().is(DamageTypes.INDIRECT_MAGIC))
		{
			event.setCanceled(true);
			target.hurt(user.damageSources().indirectMagic(user, event.getSource().getDirectEntity()), event.getAmount());
		}
	}
}
