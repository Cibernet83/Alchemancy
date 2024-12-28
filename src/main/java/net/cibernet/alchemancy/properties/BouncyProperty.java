package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber
public class BouncyProperty extends Property
{
	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		if(target == null || target.level().isClientSide())
			return;

		if(source == target && source instanceof LivingEntity user)
		{
			if(CommonUtils.calculateHitResult(user).getType() != HitResult.Type.MISS)
				knockBack(user, user.position().add(user.getLookAngle()));
			return;
		}

		Vec3 sourcePos = source != null ? source.position() :
				damageSource.getSourcePosition() != null ? damageSource.getSourcePosition() :
				damageSource.getDirectEntity() != null ? damageSource.getDirectEntity().position() : null;

		if(sourcePos != null)
			knockBack(target, sourcePos);
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
				knockBack(user, attackPos);
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
			knockBack(target, attackPos);
	}

	public static void knockBack(Entity target, Vec3 sourcePos)
	{
		target.hurtMarked = true;
		target.hasImpulse = true;
		Vec3 vec3 = target.getDeltaMovement();
		float strength = 1;
		Vec3 vec31 = sourcePos.subtract(target.position()).normalize().scale(strength);
		target.setDeltaMovement(vec3.x * 0.5 - vec31.x, vec3.y * 0.5 - vec31.y, vec3.z * 0.5 - vec31.z);
	}

	private static final HashMap<UUID, Vec3> BOUNCE_TARGETS = new HashMap<>();

	@Override
	public void onFall(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if(slot == EquipmentSlot.FEET && event.getDistance() >= 0.2f && !user.isShiftKeyDown())
		{
			event.setDamageMultiplier(0);
			if(user.level().isClientSide())
				BOUNCE_TARGETS.put(user.getUUID(), user.getDeltaMovement());
		}
	}

	@SubscribeEvent
	private static void onEntityTickPost(EntityTickEvent.Post event)
	{
		Entity user = event.getEntity();
		UUID uuid = user.getUUID();
		if( BOUNCE_TARGETS.containsKey(uuid))
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
