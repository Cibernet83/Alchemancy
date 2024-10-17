package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class MobEffectOnHitProperty extends Property
{
	public final MobEffectInstance effect;

	public MobEffectOnHitProperty(MobEffectInstance effect)
	{
		this.effect = effect;
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(!user.level().isClientSide())
			target.addEffect(new MobEffectInstance(effect));
	}

	@Override
	public int getColor(ItemStack stack) {
		return effect.getEffect().value().getColor();
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		for (LivingEntity entity : entitiesInBounds) {
			if(!entity.hasEffect(effect.getEffect().getDelegate()) || root.getTickCount() % 20 == 0)
				entity.addEffect(new MobEffectInstance(effect));
		}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, FastColor.ARGB32.color(255, effect.getEffect().value().getColor())));
	}
}
