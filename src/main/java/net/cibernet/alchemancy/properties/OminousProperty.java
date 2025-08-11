package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OminousProperty extends MobEffectEquippedAndHitProperty
{
	public OminousProperty() {
		super(new MobEffectInstance(MobEffects.BAD_OMEN, 10), EquipmentSlotGroup.ANY, false);
	}


	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if(canApplyOminous(user))
			super.onEquippedTick(user, slot, stack);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		if(root.getTickCount() % 20 == 0)
			for (LivingEntity entity : entitiesInBounds) {
				if(!entity.hasEffect(effect.getEffect().getDelegate()) && canApplyOminous(entity))
					entity.addEffect(new MobEffectInstance(effect));
			}
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

	}

	public boolean canApplyOminous(LivingEntity target) {
		return target.getActiveEffects().stream().noneMatch(effectInstance -> effectInstance.getEffect().is(AlchemancyTags.MobEffects.BLOCKS_OMINOUS));
	}
}
