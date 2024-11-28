package net.cibernet.alchemancy.properties;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MobEffectEquippedAndHitProperty extends MobEffectOnHitProperty
{
	protected final EquipmentSlotGroup validSlots;
	private final boolean italics;

	public MobEffectEquippedAndHitProperty(MobEffectInstance effect, EquipmentSlotGroup slots, boolean italics) {
		super(effect);
		validSlots = slots;
		this.italics = italics;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(validSlots.test(slot))
			user.addEffect(new MobEffectInstance(effect));
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return italics ? super.getDisplayText(stack).copy().withStyle(ChatFormatting.ITALIC) : super.getDisplayText(stack);
	}
}
