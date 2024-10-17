package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class DamageMultiplierProperty extends Property
{
	private final AttributeModifier DAMAGE_MOD;
	private final int color;
	public DamageMultiplierProperty(int color, float multiplier)
	{
		DAMAGE_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "property_damage_multiplier"), multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		this.color = color;
	}

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		event.addModifier(Attributes.ATTACK_DAMAGE, DAMAGE_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}
}