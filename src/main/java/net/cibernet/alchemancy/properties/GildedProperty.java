package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class GildedProperty extends Property
{
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "gilded_property_modifier"), 0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		event.addModifier(Attributes.MINING_EFFICIENCY, SPEED_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public TriState isItemInTag(ItemStack stack, TagKey<Item> tagKey)
	{
		return tagKey == ItemTags.PIGLIN_LOVED ? TriState.TRUE : super.isItemInTag(stack, tagKey);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xEAC81C;
	}
}
