package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class SpreadsOnHitProperty extends Property
{
	private final EquipmentSlotGroup spreadsTo;

	protected SpreadsOnHitProperty(EquipmentSlotGroup spreadsTo) {
		this.spreadsTo = spreadsTo;
	}

	public static SpreadsOnHitProperty simple(int color, EquipmentSlotGroup spreadsTo)
	{
		return new SpreadsOnHitProperty(spreadsTo) {
			@Override
			public int getColor(ItemStack stack) {
				return color;
			}
		};
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(!target.level().isClientSide())
		{
			Holder<Property> propertyHolder = AlchemancyProperties.getHolder(this);
			ArrayList<EquipmentSlot> slots = new ArrayList<>(List.of(EquipmentSlot.values()));

			Collections.shuffle(slots);
			for (EquipmentSlot slot : slots) {
				if(spreadsTo.test(slot) && !InfusedPropertiesHelper.hasProperty(target.getItemBySlot(slot), propertyHolder))
					InfusedPropertiesHelper.addProperty(target.getItemBySlot(slot), propertyHolder);
			}
		}
	}
}
