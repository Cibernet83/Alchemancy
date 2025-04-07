package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

public class FlameEmperorProperty extends Property
{
	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		if(user.isOnFire())
			event.setNewDamage(Math.max(event.getOriginalDamage() * 3, event.getNewDamage() * 1.25f));
	}

	@Override
	public void modifyCriticalAttack(Player user, ItemStack weapon, CriticalHitEvent event)
	{
		if(user.isOnFire())
			event.setDamageMultiplier(Math.max(event.getDamageMultiplier(), event.getVanillaMultiplier() * 0.5f));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0;
	}
}
