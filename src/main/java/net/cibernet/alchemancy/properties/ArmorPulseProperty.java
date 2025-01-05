package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class ArmorPulseProperty extends Property
{
	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if((slot.isArmor() || user.getUseItem() == weapon) && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) &&
				event.getSource().getEntity() != null && event.getSource().getEntity() != user &&
				event.getSource().getEntity().distanceTo(user) <= user.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE))
		{
			event.setNewDamage(Math.max(0, event.getNewDamage() - 1));
			InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().onActivation(user, user, weapon, event.getSource()));
		}
	}


	@Override
	public int getColor(ItemStack stack) {
		return 0xFF7B60;
	}
}
