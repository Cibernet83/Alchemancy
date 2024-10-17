package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class NocturnalProperty extends MobEffectEquippedAndHitProperty
{
	public NocturnalProperty()
	{
		super(new MobEffectInstance(MobEffects.NIGHT_VISION, 300), EquipmentSlotGroup.HEAD, false);
	}

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		if(user.level().canSeeSky(user.blockPosition()) && !user.level().isDay())
			event.setNewDamage(event.getNewDamage() * 1.4f);
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
	}
}
