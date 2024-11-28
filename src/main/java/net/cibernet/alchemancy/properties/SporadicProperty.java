package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SporadicProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!user.level().isClientSide() && user.getRandom().nextFloat() < 0.005f)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(user, user, stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x9A6DCE;
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.ITALIC);
	}
}
