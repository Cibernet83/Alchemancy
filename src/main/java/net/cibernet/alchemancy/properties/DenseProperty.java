package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class DenseProperty extends Property
{
	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(canSmashAttack(user))
			event.setAmount(event.getAmount() + getAttackDamageBonus(target, event.getSource()) *
					PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.ATTACK_DAMAGE, 0.65f));
	}

	public static boolean canSmashAttack(Entity entity) {
		return entity.fallDistance > 1.5F && !(entity instanceof LivingEntity living && living.isFallFlying());
	}

	public float getAttackDamageBonus(Entity target, DamageSource damageSource) {
		if (damageSource.getDirectEntity() instanceof LivingEntity livingentity) {
			if (!canSmashAttack(livingentity)) {
				return 0.0F;
			} else {
				float f1 = livingentity.fallDistance;
				float f2;
				if (f1 <= 3.0F) {
					f2 = 4.0F * f1;
				} else if (f1 <= 8.0F) {
					f2 = 12.0F + 2.0F * (f1 - 3.0F);
				} else {
					f2 = 22.0F + f1 - 8.0F;
				}

				return livingentity.level() instanceof ServerLevel serverlevel
						? f2 + EnchantmentHelper.modifyFallBasedDamage(serverlevel, livingentity.getWeaponItem(), target, damageSource, 0.0F) * f1
						: f2;
			}
		} else {
			return 0.0F;
		}
	}

	@Override
	public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if(slot == EquipmentSlot.FEET && MaceItem.canSmashAttack(entity))
		{
			entity.playSound(entity.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND, 1, 1);

			if(!entity.level().isClientSide())
			{
				if(entity instanceof ServerPlayer serverplayer)
					serverplayer.setSpawnExtraParticlesOnFall(true);

				float radius = PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.EFFECT_VALUE, 1.5f);

				for (LivingEntity target : entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius))) {
					if (target != entity && target.distanceTo(entity) <= radius) {
						DamageSource damageSource = entity instanceof Player player ? entity.damageSources().playerAttack(player) : entity.damageSources().mobAttack(entity);
						target.hurt(damageSource, getAttackDamageBonus(target, damageSource) *
								PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.EFFECT_VALUE, 0.65f));

					}
				}

				stack.hurtAndBreak((int) (entity.fallDistance * 0.5f) *
						PropertyModifierComponent.get(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION), entity, slot);
			}
		}
	}

	@Override
	public TriState isItemInTag(ItemStack stack, TagKey<Item> tagKey) {
		return tagKey == ItemTags.MACE_ENCHANTABLE ? TriState.TRUE : super.isItemInTag(stack, tagKey);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		if(entity.isInFluidType()) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.03f, 0));
			entity.hasImpulse = true;
		}

	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile entity) {

		if(entity.isInFluidType())
		{
			entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.03f, 0));
			entity.hasImpulse = true;
		}

	}

	@Override
	public void onEquippedTick(LivingEntity entity, EquipmentSlot slot, ItemStack stack)
	{
		if(entity.isInFluidType()) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.08f, 0));
			entity.hasImpulse = true;
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x99A1AE;
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(ChatFormatting.BOLD);
	}
}
