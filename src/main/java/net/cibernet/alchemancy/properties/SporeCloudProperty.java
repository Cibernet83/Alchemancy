package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SporeCloudProperty extends AbstractTimerProperty
{
	private static final float RADIUS = 4;
	private static final int COOLDOWN = 400;
	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		Entity user = source == null ? target : source;

		if(getElapsedTime(stack) > COOLDOWN)
			releaseSpores(user.level(), user, stack);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(getElapsedTime(weapon) > COOLDOWN)
			releaseSpores(user.level(), user, weapon);
	}

	@Override
	public long getElapsedTime(ItemStack stack)
	{
		return getData(stack).equals(getDefaultData()) ? CommonUtils.getLevelData().getDayTime() : super.getElapsedTime(stack);
	}

	public void releaseSpores(Level level, Entity source, ItemStack stack)
	{
		resetStartTimestamp(stack);

		RandomSource rand = level.getRandom();
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(RADIUS));

		if(level instanceof ServerLevel serverLevel)
			for(int i = 0; i < RADIUS * 20; i++)
			{
				serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(ParticleTypes.FALLING_SPORE_BLOSSOM), source.position().x, source.position().y, source.position().z, 1,
						rand.nextDouble() * RADIUS, 0, rand.nextDouble() * RADIUS, 0);
			}

		ItemStack refStack = stack.copy();
		for (LivingEntity target : entities)
			if(target != source)
				InfusedPropertiesHelper.forEachProperty(refStack, propertyHolder -> {
					if(!propertyHolder.equals(asHolder()))
						propertyHolder.value().onActivation(source, target, stack);
				});
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource)
	{
		if(getElapsedTime(root.getItem()) > COOLDOWN)
			playRootedParticles(root, randomSource, ParticleTypes.FALLING_SPORE_BLOSSOM);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x7B6D73;
	}
}
