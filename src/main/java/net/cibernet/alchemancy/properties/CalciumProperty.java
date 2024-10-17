package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class CalciumProperty extends Property
{
	private static final AttributeModifier SAFE_FALL_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "calcaerous_property_modifier"), 10F, AttributeModifier.Operation.ADD_VALUE);
	private static final AttributeModifier FALL_DAMAGE_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "calcaerous_property_modifier"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.LEGS)
		{
			event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.LEGS);
			event.addModifier(Attributes.FALL_DAMAGE_MULTIPLIER, FALL_DAMAGE_MOD, EquipmentSlotGroup.LEGS);
		}
		else if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.BODY)
		{
			event.addModifier(Attributes.SAFE_FALL_DISTANCE, SAFE_FALL_MOD, EquipmentSlotGroup.BODY);
			event.addModifier(Attributes.FALL_DAMAGE_MULTIPLIER, FALL_DAMAGE_MOD, EquipmentSlotGroup.BODY);
		}
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		if(target instanceof LivingEntity living)
			living.removeAllEffects();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xEFECDA;
	}
}
