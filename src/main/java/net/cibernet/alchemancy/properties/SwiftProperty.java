package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class SwiftProperty extends Property
{
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "swift_property_modifier"), 0.55F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.LEGS)
			event.addModifier(Attributes.MOVEMENT_SPEED, SPEED_MOD, EquipmentSlotGroup.LEGS);
		else if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.BODY)
			event.addModifier(Attributes.MOVEMENT_SPEED, SPEED_MOD, EquipmentSlotGroup.BODY);
		event.addModifier(Attributes.ATTACK_SPEED, SPEED_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {

		if(event.getDuration() != stack.getUseDuration(user))
			event.setDuration(event.getDuration()-1);
	}

	@Override
	public int getColor(ItemStack stack) {
		return MobEffects.MOVEMENT_SPEED.value().getColor();
	}
}
