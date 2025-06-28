package net.cibernet.alchemancy.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AnchoredProperty extends Property {

	@Override
	public int getPriority() {
		return Priority.HIGHEST;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if(slot.isArmor())
			user.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		itemEntity.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x244560;
	}
}
