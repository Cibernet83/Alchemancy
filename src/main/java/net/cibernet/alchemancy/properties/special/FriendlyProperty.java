package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class FriendlyProperty extends Property
{
	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(target.getType().is(AlchemancyTags.EntityTypes.AFFECTED_BY_FRIENDLY))
			event.setCanceled(true);
	}

	@Override
	public int getPriority() {
		return Priority.LOWEST;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDC989E;
	}
}
