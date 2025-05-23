package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class KineticRechargeProperty extends AbstractTimerProperty {

	private static final int BASE_TIME = 60;

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (user.level().isClientSide() || !stack.isDamageableItem()) return;

		var vel = Math.max(10, BASE_TIME - (int) Math.ceil(user.getKnownMovement().length() * 100));

		if (!stack.isDamaged() || vel >= BASE_TIME) {
			removeData(stack);
			return;
		}

		if (!hasRecordedTimestamp(stack))
			resetStartTimestamp(stack);
		else if (getElapsedTime(stack) >= vel) {
			repairItem(stack, 1);
			resetStartTimestamp(stack);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF8800;
	}
}
