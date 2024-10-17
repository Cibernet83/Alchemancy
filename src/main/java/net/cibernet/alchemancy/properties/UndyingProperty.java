package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UndyingProperty extends Property
{
	final int[] colors = new int[] {0x41E67F, 0xFDFBEE, 0xEADB84, 0xFDFBEE};

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		if(stack.getMaxDamage() >= stack.getDamageValue() + resultingAmount)
		{
			InfusedPropertiesHelper.removeProperty(stack, AlchemancyProperties.UNDYING);
			stack.setDamageValue((int) (stack.getMaxDamage() * 0.6f));

			user.level().playSound(user instanceof Player player ? player : null, user.position().x, user.position().y, user.position().z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.65f, 1f);

			return 0;
		}
		return resultingAmount;
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.5f, colors);
	}
}
