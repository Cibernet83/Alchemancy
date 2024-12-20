package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class LoyalProperty extends Property
{
	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {

		if(projectile instanceof AbstractArrow arrow)
		{
			Entity entity = arrow.getOwner();
			if(entity != null && (arrow.pickup == AbstractArrow.Pickup.ALLOWED ||
					(arrow.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && entity instanceof Player player && player.isCreative())))
			{
				if (((AbstractArrowAccessor) arrow).accessInGroundTime() > 20)
				{
					arrow.setNoPhysics(true);
					arrow.setNoGravity(true);
					arrow.setDeltaMovement(Vec3.ZERO);
					arrow.getPersistentData().putBoolean(Alchemancy.MODID + ":loyal_returning", true);
				}
				if (arrow.getPersistentData().getBoolean(Alchemancy.MODID + ":loyal_returning")) {
					int i = 1; //Loyalty Level, maybe increase with Aqua essence, or Spectra
					Vec3 vec3 = entity.getEyePosition().subtract(arrow.position());

					double d0 = 0.05 * i;
					arrow.setDeltaMovement(arrow.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
					arrow.hasImpulse = true;
					/*
					if (arrow.clientSideReturnTridentTickCount == 0) {
						arrow.playSound(AlchemancySoundEvents.LOYAL.value(), 10.0F, 1.0F);
					}

					arrow.clientSideReturnTridentTickCount++;
					 */
				}
			}
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		Entity thrower = entity.getOwner();

		if(!entity.hasPickUpDelay() && thrower != null && thrower.distanceTo(entity) < 50)
		{
			entity.setDeltaMovement(thrower.position().subtract(entity.position()).normalize().scale(0.125f));
			entity.hasImpulse = true;
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x66C4AF;
	}
}
