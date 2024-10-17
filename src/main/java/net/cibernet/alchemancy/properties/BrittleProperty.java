package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public class BrittleProperty extends DurabilityMultiplierProperty
{
	public BrittleProperty()
	{
		super(0xD0EAE9, 0.65f);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		if(!itemEntity.level().isClientSide() && itemEntity.getDeltaMovement().length() >= 0.3 && ProjectileUtil.getHitResultOnMoveVector(itemEntity, entity -> false).getType() == HitResult.Type.BLOCK)
		{
			breakItem(itemEntity, (ServerLevel) itemEntity.level());
		}
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		breakProjectile(stack, projectile);
	}

	public static void breakProjectile(ItemStack stack, Projectile projectile)
	{

		if(!projectile.level().isClientSide)
			breakItem(projectile, stack, (ServerLevel) projectile.level());
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEntityItemDestroyed(stack, projectile, projectile.damageSources().generic()));
		projectile.discard();
	}

	private static void breakItem(ItemEntity itemEntity, ServerLevel level)
	{
		ItemStack stack = itemEntity.getItem();
		breakItem(itemEntity, stack, level);
		stack.onDestroyed(itemEntity, itemEntity.damageSources().generic());
		itemEntity.discard();
	}

	private static void breakItem(Entity entity, ItemStack stack, ServerLevel level) {

		if (!stack.isEmpty()) {
			if (!entity.isSilent()) {
				level.playSeededSound(null, entity.position().x, entity.position().y, entity.position().z,
						level.registryAccess().holderOrThrow(ResourceKey.create(Registries.SOUND_EVENT, stack.getBreakingSound().getLocation()))
						, entity.getSoundSource(), 0.8f, 0.8F + level.random.nextFloat() * 0.4F, level.random.nextLong());
			}

			spawnItemParticles(stack, 5, entity, level);
		}
	}

	private static void spawnItemParticles(ItemStack stack, int amount, Entity itemEntity, ServerLevel level)
	{
		for (int i = 0; i < amount; i++) {
			Vec3 vec3 = new Vec3(((double)itemEntity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3 = vec3.xRot(-itemEntity.getXRot() * (float) (Math.PI / 180.0));
			vec3 = vec3.yRot(-itemEntity.getYRot() * (float) (Math.PI / 180.0));
			double d0 = (double)(-itemEntity.getRandom().nextFloat()) * 0.6 - 0.3;

			level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), itemEntity.position().x, itemEntity.position().y, itemEntity.position().z, 1, vec3.x, vec3.y + 0.05, vec3.z, 0.1);
		}
	}
}
