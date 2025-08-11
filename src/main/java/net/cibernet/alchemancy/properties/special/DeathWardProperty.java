package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.network.S2CDeathWardEffectsPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeathWardProperty extends Property
{
	@Override
	public void onUserDeath(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingDeathEvent event)
	{
		if(!event.isCanceled() && CommonHooks.onLivingUseTotem(entity, event.getSource(), stack, slot == EquipmentSlot.OFFHAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND))
		{
			if(stack.isDamageableItem())
				stack.hurtAndBreak(500, entity, slot);
			else consumeItem(entity, stack, slot);

			entity.setHealth(1.0F);
			event.setCanceled(true);

			entity.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
			entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
			entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CDeathWardEffectsPayload(entity, stack));
		}
	}

	final int[] colors = new int[] {0x41E67F, 0xFDFBEE, 0xEADB84, 0xFDFBEE};
	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.5f, colors);
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
