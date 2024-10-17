package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VengefulProperty extends Property
{
	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event) {
		if(user instanceof LivingEntity living)
		{
			boolean apply = event.getEntity().equals(living.getLastHurtByMob());
			event.setNewDamage(event.getNewDamage() * (apply ? 1.75f : 0.85f));
			if(apply)
				ClientUtil.createTrackedParticles(event.getEntity(), ParticleTypes.ENCHANTED_HIT);
		}

	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0;
	}
}
