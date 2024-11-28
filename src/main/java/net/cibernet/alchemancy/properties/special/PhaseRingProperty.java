package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PhaseRingProperty extends Property
{

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		user.noPhysics = true;
		user.setOnGround(false);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC89FFF;
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}
}
