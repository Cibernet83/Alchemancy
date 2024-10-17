package net.cibernet.alchemancy.properties;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

public class GrapplingProperty extends Property
{
	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		Vec3 attackPos = damageSource.getSourcePosition();
		if(attackPos == null && damageSource.getEntity() != null)
			attackPos = damageSource.getEntity().position();

		if(attackPos != null)
		{
			float pStrength = (float) (target.position().distanceTo(attackPos)) * .2f;

			target.hurtMarked = true;
			Vec3 vec3 = target.getDeltaMovement();
			Vec3 vec31 = target.position().subtract(attackPos).normalize().scale(pStrength);

			if(target.onGround() && vec31.y > -.2)
				vec31 = vec31.add(0, -.2, 0);
			target.setDeltaMovement(vec3.x / 2.0D - vec31.x, vec3.y / 2.0D - vec31.y, vec3.z / 2.0D - vec31.z);
		}
	}

	@Override
	public void modifyKnockBackApplied(LivingEntity user, ItemStack weapon, LivingEntity target, LivingKnockBackEvent event) {
		event.setCanceled(true);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x00B5B5;
	}
}
