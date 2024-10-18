package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Fireworks;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class ExtendedProperty extends Property
{
	private static final AttributeModifier STEP_HEIGHT_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "extended_property_modifier"), 0.5F, AttributeModifier.Operation.ADD_VALUE);
	private static final AttributeModifier REACH_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "extended_property_modifier"), 2, AttributeModifier.Operation.ADD_VALUE);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()) == EquipmentSlot.FEET)
			event.addModifier(Attributes.STEP_HEIGHT, STEP_HEIGHT_MOD, EquipmentSlotGroup.FEET);
		event.addModifier(Attributes.BLOCK_INTERACTION_RANGE, REACH_MOD, EquipmentSlotGroup.MAINHAND);
		event.addModifier(Attributes.ENTITY_INTERACTION_RANGE, REACH_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.FIREWORKS && data instanceof Fireworks fireworks)
			return new Fireworks(fireworks.flightDuration(), fireworks.explosions());
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return result * 2;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xBC9862;
	}
}
