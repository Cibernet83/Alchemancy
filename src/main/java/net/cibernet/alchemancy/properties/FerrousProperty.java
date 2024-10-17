package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class FerrousProperty extends Property
{

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event) {

		if(slot.isArmor() && !weapon.is(AlchemancyTags.Items.INCREASES_SHOCK_DAMAGE_RECEIVED) && event.getSource().is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE))
			event.setNewDamage(event.getNewDamage() + event.getOriginalDamage() * 0.25f);
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.MAX_DAMAGE && data instanceof Integer)
			return ((Integer)data) + 150;
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD8D8D8;
	}
}
