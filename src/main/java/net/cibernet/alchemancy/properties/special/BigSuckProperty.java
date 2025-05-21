package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.Nullable;

public class BigSuckProperty extends Property {
	@Override
	public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {

		suckBigly(level, user);

		if (PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem())) {
			int durabilityConsumed = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20);
			if (user != null)
				stack.hurtAndBreak(durabilityConsumed, user, EquipmentSlot.MAINHAND);
			else if (user.level() instanceof ServerLevel serverLevel)
				stack.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {
				});
		}

		return false;
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {

		if(target == source) return;

		suckBigly(target.level(), target);
		if (PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem())) {
			int durabilityConsumed = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20);
			if (target instanceof LivingEntity living)
				stack.hurtAndBreak(durabilityConsumed, living, EquipmentSlot.MAINHAND);
			else if (target.level() instanceof ServerLevel serverLevel)
				stack.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {
				});
		} else consumeItem(target, stack, EquipmentSlot.MAINHAND);
	}

	@Override
	public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack) {

		if (!(target.level() instanceof ServerLevel serverLevel)) return;

		boolean pickUp = false;
		for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), item -> true)) {
			item.moveTo(position.getBottomCenter());
			item.setDeltaMovement(Vec3.ZERO);
			pickUp = true;
		}

		if (pickUp && PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, stack.isDamageableItem())) {
			int durabilityConsumed = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20);
			stack.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {
			});
		}
	}

	private void suckBigly(Level level, Entity user) {
		if (level instanceof ServerLevel serverLevel) {
			if (user instanceof Player player)
				for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), item -> true)) {
					item.playerTouch(player);
				}
			else
				for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), item -> true)) {
					item.moveTo(user.position());
					item.setDeltaMovement(Vec3.ZERO);
				}
		}
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {

		if (projectile.isRemoved() || event.isCanceled() || !(projectile.level() instanceof ServerLevel serverLevel))
			return;

		boolean pickedUp = false;
		for (ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), item -> true)) {
			item.moveTo(rayTraceResult.getLocation());
			pickedUp = true;
		}

		if (pickedUp) projectile.discard();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF00FF;
	}
}
