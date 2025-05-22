package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.entangled.AbstractEntangledProperty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class QuantumShiftProperty extends Property {

	@Override
	public void onStackedOverItem(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event) {

		AtomicBoolean cont = new AtomicBoolean(true);
		InfusedPropertiesHelper.forEachProperty(stackedOnItem, propertyHolder -> {
			if(cont.get() && propertyHolder.value() instanceof AbstractEntangledProperty entangledProperty)
			{
				event.getSlot().set(entangledProperty.shift(stackedOnItem));
				cont.set(false);
				event.setCanceled(true);
			}
		});
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
		super.onActivation(source, target, stack, damageSource);

		if(target instanceof LivingEntity living) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack stackInSlot = living.getItemBySlot(slot);

				AtomicBoolean cont = new AtomicBoolean(true);
				InfusedPropertiesHelper.forEachProperty(stackInSlot, propertyHolder -> {
					if(cont.get() && propertyHolder.value() instanceof AbstractEntangledProperty entangledProperty)
					{
						living.setItemSlot(slot, entangledProperty.shift(stackInSlot));
						cont.set(false);
					}
				});
			}
		}
		else if(target instanceof InfusedItemProjectile projectile)
		{
			AtomicBoolean cont = new AtomicBoolean(true);

			InfusedPropertiesHelper.forEachProperty(projectile.getItem(), propertyHolder -> {
				if(cont.get() && propertyHolder.value() instanceof AbstractEntangledProperty entangledProperty)
				{
					projectile.setItem(entangledProperty.shift(projectile.getItem()));
					cont.set(false);
				}
			});
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE5FF00;
	}
}
