package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class GliderProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(!user.level().isClientSide() && slot == EquipmentSlot.CHEST && user.isFallFlying())
		{
			int nextFlightTick = user.getFallFlyingTicks() + 1;
			if (nextFlightTick % 10 == 0) {
				if (nextFlightTick % 20 == 0) {
					stack.hurtAndBreak(1, user, net.minecraft.world.entity.EquipmentSlot.CHEST);
				}
				user.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_GLIDE);
			}

		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x8F8FB3;
	}
}
