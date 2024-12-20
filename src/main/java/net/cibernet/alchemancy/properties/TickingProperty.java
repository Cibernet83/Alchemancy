package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TickingProperty extends AbstractTimerProperty
{

	private static final float TIMER_DURATION = 200;

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		float percentageTimeLeft = getPercentageTimeLeft(stack);
		if(percentageTimeLeft > 0)
		{
			if(System.currentTimeMillis() % (int)(1000) == 0)
				user.playSound(AlchemancySoundEvents.TICKING.value(), 0.2f, 0.8f);
		}
		if(percentageTimeLeft == 0)
		{
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
			{
				if (!propertyHolder.equals(asHolder()))
					propertyHolder.value().onActivation(user, user, stack);
			});
			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.INTERACTABLE))
				resetStartTimestamp(stack);
		}
	}

	@Override
	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity)
	{
		resetStartTimestamp(stack);
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		resetStartTimestamp(stack);
	}

	public float getPercentageTimeLeft(ItemStack stack)
	{
		if(getData(stack) == 0)
			return -1;

		return 1 - Math.max(0, getElapsedTime(stack) / TIMER_DURATION);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		if(getData(stack) == 0)
			return getTickingColor(1);
		float timeLeft = getPercentageTimeLeft(stack);
		if(timeLeft <= 0)
			return 0x410500;
		return getTickingColor(1);
	}

	private int getTickingColor(float speed)
	{
		return (System.currentTimeMillis() / (int)(speed * 1000)) % 2 == 0 ? 0xFF0000 : 0xFAD64A;
	}
}
