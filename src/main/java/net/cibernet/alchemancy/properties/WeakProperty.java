package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class WeakProperty extends Property
{
	private static final AttributeModifier ARMOR_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "weak_property_modifier"), -1F, AttributeModifier.Operation.ADD_VALUE);

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event) {
		event.setNewDamage(event.getNewDamage() * 0.5f);
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if(slot.isArmor())
			event.setNewDamage(event.getNewDamage() * 1.2f);
	}

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()).isArmor())
			event.addModifier(Attributes.ARMOR, ARMOR_MOD, EquipmentSlotGroup.ARMOR);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		return MobEffects.WEAKNESS.value().getColor();
	}
}
