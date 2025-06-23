package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.ItemEntityAccessor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public class LazyProperty extends Property
{
	@Override
	public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, ItemEntityPickupEvent.Pre event) {
		event.setCanPickup(TriState.FALSE);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {

		if(!itemEntity.getPersistentData().getBoolean(Alchemancy.MODID+":lazy_property_processed"))
		{
			itemEntity.setPickUpDelay(Math.max(80, ((ItemEntityAccessor)itemEntity).getPickupDelay() * 2));
			itemEntity.getPersistentData().putBoolean(Alchemancy.MODID+":lazy_property_processed", true);
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.85f));
	}
	
	@Override
	public int getColor(ItemStack stack) {
		return 0xB56955;
	}
}
