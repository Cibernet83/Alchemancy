package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class BuoyantProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
		if(entity.isInFluidType()) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.03f, 0));
			entity.hasImpulse = true;
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{if(entity.isInFluidType()) {
		entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.03f, 0));
		entity.hasImpulse = true;
	}

	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile entity) {
		if(entity.isInFluidType()) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.04f, 0));
			entity.hasImpulse = true;
		}

	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE2CF78;
	}
}
