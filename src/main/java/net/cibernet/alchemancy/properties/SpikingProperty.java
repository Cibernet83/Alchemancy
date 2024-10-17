package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.jetbrains.annotations.Nullable;

public class SpikingProperty extends Property
{

	@Override
	public float modifyStepOnFriction(Entity user, ItemStack stack, float originalResult, float result) {
		return 0.55f;
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {
		target.setDeltaMovement(0,-2f, 0);
	}

	@Override
	public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event)
	{
		target.setDeltaMovement(0,-2f, 0);
		event.setCanceled(true);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x937F7F;
	}
}
