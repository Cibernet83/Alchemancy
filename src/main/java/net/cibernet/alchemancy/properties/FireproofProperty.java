package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class FireproofProperty extends Property
{
	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.FIRE_RESISTANT)
			return true;
		return super.modifyDataComponent(stack, dataType, data);
	}


	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(slot.isArmor() && user.isOnFire())
			user.setRemainingFireTicks(Math.min(240, user.tickCount % 5 == 0 ? user.getRemainingFireTicks() - 10 : user.getRemainingFireTicks()));
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.clearFire();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xEAA727;
	}

	@Override
	public int getPriority() {
		return Priority.LOW;
	}
}
