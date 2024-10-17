package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class JaggedProperty extends Property
{
	@Override
	public void onItemPickedUp(Player target, ItemStack stack, ItemEntity itemEntity)
	{
		DamageSource thornsDamage = target.damageSources().thorns(itemEntity);
		if(!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.NONLETHAL))
			target.hurt(thornsDamage, InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHARP) ? 2 : 1);
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onAttack(itemEntity, stack, thornsDamage, target));
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if((slot.isArmor() || user.getUseItem() == weapon) && damageSource.getDirectEntity() != null)
		{
			Entity target = damageSource.getDirectEntity();

			if(target instanceof LivingEntity livingTarget)
			{
				DamageContainer thornsDamage = new DamageContainer(user.damageSources().thorns(user), (float) getItemAttackDamage(weapon));

				//i might regret this
				LivingIncomingDamageEvent incomingDamageEvent = new LivingIncomingDamageEvent(livingTarget, thornsDamage);
				InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().onIncomingAttack(user, weapon, livingTarget, incomingDamageEvent));

				if(!incomingDamageEvent.isCanceled())
				{
					LivingDamageEvent.Pre damageEvent = new LivingDamageEvent.Pre(livingTarget, thornsDamage);
					InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().modifyAttackDamage(user, weapon, damageEvent));
					livingTarget.hurt(damageEvent.getSource(), damageEvent.getNewDamage());
				}


				//if(!InfusedPropertiesHelper.hasProperty(weapon, AlchemancyProperties.NONLETHAL))
				//	target.hurt(thornsDamage, InfusedPropertiesHelper.hasProperty(weapon, AlchemancyProperties.SHARP) ? 2 : 1);
				//InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().onAttack(user, weapon, thornsDamage, livingTarget));
			}
		}
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		if(root.getTickCount() % 10 == 0 && !InfusedPropertiesHelper.hasProperty(root.getItem(), AlchemancyProperties.NONLETHAL))
			for (LivingEntity entity : entitiesInBounds) {
				entity.hurt(entity.damageSources().cactus(), InfusedPropertiesHelper.hasProperty(root.getItem(), AlchemancyProperties.SHARP) ? 2 : 1);
			}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.CRIT);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x649832;
	}
}
