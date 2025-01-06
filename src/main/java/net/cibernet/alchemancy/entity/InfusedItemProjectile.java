package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class InfusedItemProjectile extends ThrowableItemProjectile
{
	public InfusedItemProjectile(Level level) {
		super(AlchemancyEntities.ITEM_PROJECTILE.get(), level);
	}

	public InfusedItemProjectile(double x, double y, double z, Level level) {
		super(AlchemancyEntities.ITEM_PROJECTILE.get(), x, y, z, level);
	}

	public InfusedItemProjectile(LivingEntity shooter, Level level) {
		super(AlchemancyEntities.ITEM_PROJECTILE.get(), shooter, level);
	}

	public InfusedItemProjectile(EntityType<? extends InfusedItemProjectile> entityType, Level level)
	{
		super(entityType, level);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}

	private ParticleOptions getParticle() {
		ItemStack itemstack = this.getItem();
		return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
				? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
				: ParticleTypes.ITEM_SNOWBALL;
	}

	/**
	 * Handles an entity event received from a {@link net.minecraft.network.protocol.game.ClientboundEntityEventPacket}.
	 */
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 3) {
			ParticleOptions particleoptions = this.getParticle();

			for (int i = 0; i < 8; i++) {
				this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	/**
	 * Called when the arrow hits an entity
	 */
	@Override
	protected void onHitEntity(EntityHitResult result)
	{
		super.onHitEntity(result);
		ItemStack stack = getItem();
		Entity entity = result.getEntity();
		DamageSource damageSource = this.damageSources().thrown(this, this.getOwner());

		entity.hurt(damageSource, (float)(Property.getItemAttackDamage(stack) - 1));
	}

	/**
	 * Called when this EntityFireball hits a block or entity.
	 */
	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide && !isRemoved())
		{
			ItemStack stack = getItem();

			if(stack.isDamageableItem())
			{
				if (level() instanceof ServerLevel serverLevel)
					stack.hurtAndBreak(20, serverLevel, null, (item) -> {
					});
			}
			else stack.shrink(1);

			if(stack.isEmpty())
			{
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEntityItemDestroyed(stack, this, damageSources().generic()));
				this.level().broadcastEntityEvent(this, (byte) 3);
			}
			else level().addFreshEntity(new ItemEntity(level(), position().x, position().y, position().z, stack));

			this.discard();
		}
	}

}
