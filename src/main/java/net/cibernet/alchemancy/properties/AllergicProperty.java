package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class AllergicProperty extends AbstractTimerProperty
{

	private boolean processing = false;

	@Override
	public void onMobEffectAdded(ItemStack stack, EquipmentSlot slot, LivingEntity user, MobEffectEvent.Added event)
	{
		if (!processing && isAvailable(stack) && event.getOldEffectInstance() == null)
		{
			processing = true;
			resetStartTimestamp(stack);
			user.level().playSound(null, user.getX(), user.getY(), user.getZ(), AlchemancySoundEvents.ALLERGIC.value(), SoundSource.PLAYERS, 0.5f, 1);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(user, user, stack));
			processing = false;
		}
	}

	public boolean isAvailable(ItemStack stack)
	{
		return !hasRecordedTimestamp(stack) || getElapsedTime(stack) > 5 || getElapsedTime(stack) <= 0;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x6DD10C;
	}

}
