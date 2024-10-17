package net.cibernet.alchemancy.util;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


public class ShockUtils
{
	static ResourceKey<DamageType> SHOCK_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "shock"));

	public static void selfDamagingMeleeShockAttack(Entity source, Vec3 position, float power)
	{
		customShockAttack(source.level(), null, position, power, (target) -> meleeShockDamage(source, position));
	}

	public static void meleeShockAttack(Entity source, Vec3 position, float power)
	{
		customShockAttack(source.level(), source, position, power, (target) -> meleeShockDamage(source, position));
	}

	public static void meleeShockAttack(Entity source, LivingEntity mainTarget, float power)
	{
		customShockAttack(source.level(), source, mainTarget.position(), power, (target) -> meleeShockDamage(source, target.position()));
	}

	public static void rangedShockAttack(Entity owner, Entity directSource, LivingEntity mainTarget, float power)
	{
		customShockAttack(directSource.level(), directSource, mainTarget.position(), power, (target) -> rangedShockDamage(owner, directSource, target));
	}

	public static void environmentalShockAttack(Level level, Vec3 position, float power)
	{
		customShockAttack(level, null, position, power, (target) -> environmentalShockDamage(level.damageSources(), position));
	}

	public static void customShockAttack(Level level, @Nullable Entity source, Vec3 position, float power, Function<LivingEntity, DamageSource> damageSourceSupplier)
	{
		double radiusSqr = Math.pow(power, 2);
		for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, CommonUtils.boundingBoxAroundPoint(position, power))) {

			if(target == source)
				continue;
			double distanceTo = target.position().equals(position) ? 1 : 1 - (target.distanceToSqr(position) / radiusSqr);
			if(distanceTo <= 1)
			{
				float damage = power;
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					if(slot.isArmor() && target.getItemBySlot(slot).is(AlchemancyTags.Items.INCREASES_SHOCK_DAMAGE_RECEIVED))
						damage += power * 0.25f;
				}

				if(target.isInWater())
					damage *= 1.5f;

				target.hurt(damageSourceSupplier.apply(target), damage);
			}
		}
	}

	public static DamageSource shockDamage(DamageSources damageSources, @Nullable Entity source, @Nullable Entity directEntity, Vec3 position)
	{
		return new DamageSource(damageSources.damageTypes.getHolderOrThrow(SHOCK_DAMAGE_KEY), source, directEntity, position);
	}

	public static DamageSource rangedShockDamage(Entity source, Entity directEntity, @NonNull LivingEntity mainTarget)
	{
		return shockDamage(source.damageSources(), source, directEntity, mainTarget.position());
	}

	public static DamageSource meleeShockDamage(Entity source, Vec3 position)
	{
		return shockDamage(source.damageSources(), source, source, position);
	}

	public static DamageSource environmentalShockDamage(DamageSources damageSources, Vec3 position)
	{
		return shockDamage(damageSources, null, null, position);
	}

}
