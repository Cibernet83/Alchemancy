package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class BurningProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xFF4E00;
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 100));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(slot.isArmor() && user.tickCount % 10 == 0)
			user.setRemainingFireTicks(Math.max(user.getRemainingFireTicks(), 20));
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		for (LivingEntity entity : entitiesInBounds) {
			entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 80));
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setRemainingFireTicks(80);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.FLAME);
	}
}
