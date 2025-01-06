package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

import java.util.UUID;

public class LoyalProperty extends Property implements IDataHolder<UUID>
{
	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(projectile.level().isClientSide())
			return;

		if(isReturning(projectile) && projectile.getOwner() != null)
		{
			Entity entity = projectile.getOwner();

			float i = 2; //Speed at which projectile returns, could be upgraded by something...
			Vec3 vec3 = entity.getEyePosition().subtract(projectile.position());

			double d0 = 0.05 * i;
			projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
			projectile.hasImpulse = true;
		}
	}

	public boolean isReturning(Entity projectile)
	{
		return projectile.getPersistentData().getBoolean(Alchemancy.MODID + ":loyal_returning");
	}

	public boolean canTriggerImpactEffects(Projectile projectile, HitResult hitResult)
	{
		return !(hitResult.getType() == HitResult.Type.ENTITY && isReturning(projectile));
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(isReturning(projectile))
		{
			if(rayTraceResult.getType() == HitResult.Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult)
			{
				if(entityHitResult.getEntity() == projectile.getOwner())
				{
					ItemEntity droppedItem = new ItemEntity(projectile.level(), projectile.position().x, projectile.position().y, projectile.position().z, stack.copy());
					droppedItem.setNoPickUpDelay();
					projectile.level().addFreshEntity(droppedItem);
					projectile.discard();
				}
				event.setCanceled(true);
			}
		}
		else
		{
			if(rayTraceResult.getType() == HitResult.Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult)
			{
				Entity entity = entityHitResult.getEntity();
				DamageSource damageSource = projectile.damageSources().thrown(projectile, projectile.getOwner());
				entity.hurt(damageSource, (float)(Property.getItemAttackDamage(stack) - 1));
			}

			projectile.getPersistentData().putBoolean(Alchemancy.MODID + ":loyal_returning", true);
			event.setCanceled(true);
			projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1));
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		if(entity.level().isClientSide() || !(entity.level() instanceof ServerLevel serverLevel))
			return;

		UUID ownerUuid = getData(stack);
		Entity owner = ownerUuid == null ? null : serverLevel.getEntity(ownerUuid);

		if(!entity.hasPickUpDelay() && owner != null && owner.distanceTo(entity) < 50)
		{
			entity.setDeltaMovement(owner.position().subtract(entity.position()).normalize().scale(0.25f));
			entity.hasImpulse = true;
		}
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		setData(stack, user.getUUID());
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x66C4AF;
	}

	@Override
	public UUID readData(CompoundTag tag) {
		return tag.getUUID("owner");
	}

	@Override
	public CompoundTag writeData(UUID data) {
		return new CompoundTag(){{putUUID("owner", data);}};
	}

	@Override
	public UUID getDefaultData() {
		return null;
	}
}
