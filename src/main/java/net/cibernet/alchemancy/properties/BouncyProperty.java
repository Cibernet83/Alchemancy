package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber
public class BouncyProperty extends Property
{
	private static final float ATTACK_BOUNCE_STRENGTH = 1.5f;

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		if(target == null || target.level().isClientSide())
			return;

		if(source == target && source instanceof Player user)
		{
			if(CommonUtils.calculateHitResult(user).getType() != HitResult.Type.MISS)
				knockBack(user, user.position().add(user.getLookAngle()), ATTACK_BOUNCE_STRENGTH);
			return;
		}

		Vec3 sourcePos = source != null ? source.position() :
				damageSource.getSourcePosition() != null ? damageSource.getSourcePosition() :
				damageSource.getDirectEntity() != null ? damageSource.getDirectEntity().position() : null;

		if(sourcePos != null)
			knockBack(target, sourcePos, ATTACK_BOUNCE_STRENGTH);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(slot.isArmor() || user.getUseItem() == weapon)
		{

			Vec3 attackPos = damageSource.getSourcePosition();
			if(attackPos == null && damageSource.getDirectEntity() != null)
				attackPos = damageSource.getDirectEntity().position();

			if(attackPos != null)
				knockBack(user, attackPos, 1);
		}
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

		Vec3 attackPos = damageSource.getSourcePosition();
		if(attackPos == null && damageSource.getDirectEntity() != null)
			attackPos = damageSource.getDirectEntity().position();
		if(attackPos == null && user != null)
			attackPos = user.position();
		if(attackPos != null)
			knockBack(target, attackPos, ATTACK_BOUNCE_STRENGTH);
	}

	public static void knockBack(Entity target, Vec3 sourcePos, float strength)
	{
		target.hurtMarked = true;
		target.hasImpulse = true;
		Vec3 targetDelta = target.getDeltaMovement();
		Vec3 knockback = sourcePos.subtract(target.position()).normalize().scale(strength);

		if(target.onGround())
			knockback = new Vec3(knockback.x(), Math.max(0.02f, knockback.y()), knockback.z());

		target.setDeltaMovement(targetDelta.x * 0.5 - knockback.x, targetDelta.y * 0.5 - knockback.y, targetDelta.z * 0.5 - knockback.z);
	}

	private static final HashMap<UUID, Vec3> BOUNCE_TARGETS = new HashMap<>();

	@Override
	public void onFall(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if((slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) && event.getDistance() >= 0.2f && !user.isShiftKeyDown())
		{
			event.setDamageMultiplier(0);
			if(!(user instanceof Player) || user.level().isClientSide())
				BOUNCE_TARGETS.put(user.getUUID(), user.getDeltaMovement());
		}
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {

		if(rayTraceResult.getType() != HitResult.Type.BLOCK || projectile.getDeltaMovement().lengthSqr() < 0.2f) return;

		Direction face = ((BlockHitResult)rayTraceResult).getDirection();

		projectile.setDeltaMovement(projectile.getDeltaMovement().multiply(switch (face.getAxis()) {
			case X -> new Vec3(-0.5, 1, 1);
			case Y -> new Vec3(1, -0.5, 1);
			case Z -> new Vec3(1, 1, -0.5);
		}));

		event.setCanceled(true);
	}

	@SubscribeEvent
	private static void onEntityTickPost(EntityTickEvent.Post event)
	{
		Entity user = event.getEntity();
		UUID uuid = user.getUUID();
		if( BOUNCE_TARGETS.containsKey(uuid) && BOUNCE_TARGETS.get(uuid) != null)
		{
			user.hurtMarked = true;
			user.setDeltaMovement(BOUNCE_TARGETS.get(uuid).multiply(1, -0.8, 1));
			user.playSound((BOUNCE_TARGETS.get(uuid).length() > 0.7) ? AlchemancySoundEvents.BOUNCY.value() : AlchemancySoundEvents.BOUNCY_SMALL.value());
			BOUNCE_TARGETS.remove(uuid);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x9EEF92;
	}
}
