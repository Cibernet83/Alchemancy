package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class SoulHarvesterProperty extends Property {

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event) {

		var effectPctg = 1 - Math.clamp(target.getHealth() / target.getMaxHealth() * 1.25f, 0, 1);

		event.setAmount(event.getAmount() + event.getAmount() * (effectPctg * 0.8f - 0.25f));
	}

	@Override
	public void onKill(LivingEntity target, LivingEntity user, ItemStack stack, LivingDeathEvent event) {
		user.heal(target.getMaxHealth() * 0.1f);
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(1.5f, 0x545400, 0x00885D);
	}
}
