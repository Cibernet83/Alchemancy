package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
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

import java.util.List;

public class SporeCloudProperty extends Property
{
	private static final float RADIUS = 5;

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(user instanceof Player player)
		{
			ItemCooldowns cooldowns = player.getCooldowns();
			if(!cooldowns.isOnCooldown(weapon.getItem()))
			{
				releaseSpores(player.level(), player, weapon);
				cooldowns.addCooldown(weapon.getItem(), 4000);
			}
		}
		else releaseSpores(user.level(), user, weapon);
	}

	public static void releaseSpores(Level level, Entity source, ItemStack stack)
	{
		RandomSource rand = level.getRandom();
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, source.getBoundingBox().inflate(RADIUS));
		DamageSource damageSource = source.damageSources().generic();

		if(level instanceof ServerLevel serverLevel)
			for(int i = 0; i < 20; i++)
			{
				serverLevel.sendParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, source.position().x, source.position().y, source.position().z, 1,
						0, 0, 0, rand.nextDouble() * 0.5);
			}

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
		{
			for (LivingEntity target : entities) {
				if(target.distanceTo(source) <= RADIUS)
				{
					propertyHolder.value().onAttack(source, stack, damageSource, target);
				}
			}
		});
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x7B6D73;
	}
}
