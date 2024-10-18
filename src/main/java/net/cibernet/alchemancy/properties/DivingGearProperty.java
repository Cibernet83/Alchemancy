package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class DivingGearProperty extends Property
{
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "aquatic_property_modifier"), 0.55F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	private static final AttributeModifier MINING_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "aquatic_property_modifier"), 2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	private static final AttributeModifier OXYGEN_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "aquatic_property_modifier"), 2F, AttributeModifier.Operation.ADD_VALUE);
	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		EquipmentSlot slot = getEquipmentSlotForItem(event.getItemStack());

		if(slot == EquipmentSlot.BODY)
			event.addModifier(Attributes.OXYGEN_BONUS, OXYGEN_MOD, EquipmentSlotGroup.BODY);
		if(slot == EquipmentSlot.HEAD)
			event.addModifier(Attributes.OXYGEN_BONUS, OXYGEN_MOD, EquipmentSlotGroup.CHEST);
		else if(slot == EquipmentSlot.CHEST)
			event.addModifier(Attributes.SUBMERGED_MINING_SPEED, SPEED_MOD, EquipmentSlotGroup.CHEST);
		else if(slot == EquipmentSlot.LEGS)
			event.addModifier(NeoForgeMod.SWIM_SPEED, SPEED_MOD, EquipmentSlotGroup.LEGS);
		else if(slot == EquipmentSlot.FEET)
			event.addModifier(Attributes.WATER_MOVEMENT_EFFICIENCY, SPEED_MOD, EquipmentSlotGroup.FEET);
		else if(slot == EquipmentSlot.MAINHAND)
			event.addModifier(Attributes.SUBMERGED_MINING_SPEED, MINING_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(projectile.isInWater() && projectile instanceof AbstractArrow)
		{
			projectile.setDeltaMovement(projectile.getDeltaMovement().scale(1 / ((AbstractArrowAccessor)projectile).invokeGetWaterInertia() * 0.99));
			projectile.hasImpulse = true;
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return MobEffects.WATER_BREATHING.value().getColor();
	}
}
