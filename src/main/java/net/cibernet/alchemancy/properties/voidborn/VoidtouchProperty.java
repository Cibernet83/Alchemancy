package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.BrittleProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.Nullable;

public class VoidtouchProperty extends Property {

	public static final int DURABILITY_CONSUMED = 800;
	public static final ResourceKey<DamageType> VOIDTOUCH_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "voidtouch"));

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

		destroyEntity(target, user);

		if (!PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, false))
			consumeItem(user, weapon, EquipmentSlot.MAINHAND);
		else if (weapon.isDamageableItem() && user instanceof LivingEntity living)
			weapon.hurtAndBreak(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, DURABILITY_CONSUMED), living,
					EquipmentSlot.MAINHAND);
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {

		if (!(rayTraceResult instanceof EntityHitResult entityHitResult)) return;

		destroyEntity(entityHitResult.getEntity(), projectile.getOwner(), projectile);

		if (event.isCanceled() && stack.isDamageableItem()) {
			if (projectile.level() instanceof ServerLevel serverLevel)
				stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, DURABILITY_CONSUMED), serverLevel, null, (item) -> {
					BrittleProperty.spawnItemParticles(stack, 5, projectile, serverLevel);
				});
		} else event.setCanceled(false);
	}

	public static DamageSource voidDamage(DamageSources damageSources, @Nullable Entity source, @Nullable Entity directEntity, Vec3 position) {
		return new DamageSource(damageSources.damageTypes.getHolderOrThrow(VOIDTOUCH_DAMAGE_KEY), source, directEntity, position);
	}

	private void destroyEntity(Entity target, @Nullable Entity user) {
		destroyEntity(target, user, user);
	}

	private void destroyEnderDragon(EnderDragon enderDragon, @Nullable Entity user, @Nullable Entity directSource) {
		enderDragon.hurt(voidDamage(enderDragon.damageSources(), user, directSource, directSource == null ? enderDragon.position() : directSource.position()), Float.MAX_VALUE);
		if (enderDragon.getDragonFight() != null)
			enderDragon.getDragonFight().setDragonKilled(enderDragon);
		enderDragon.discard();
	}

	private void destroyEntity(Entity target, @Nullable Entity user, @Nullable Entity directSource) {

		if(target.level().isClientSide() ||
				(target instanceof LivingEntity living && InfusedPropertiesHelper.hasItemWithProperty(living, AlchemancyProperties.VOIDBORN, true)))
			return;

		switch (target) {
			case EnderDragon enderDragon -> destroyEnderDragon(enderDragon, user, directSource);
			case EnderDragonPart enderDragonPart -> destroyEnderDragon(enderDragonPart.parentMob, user, directSource);
			case Player player -> {
				target.hurt(voidDamage(target.damageSources(), user, directSource, directSource == null ? target.position() : directSource.position()), Float.MAX_VALUE);
				if (player.isDeadOrDying())
					player.discard();
			}
			case null, default -> target.discard();
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(1, 0x2700A8, 0x4A0072);
	}
}
