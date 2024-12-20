package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

public class CluelessProperty extends Property
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		AtomicBoolean performed = new AtomicBoolean(false);

		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
		{
			if(propertyHolder.value() instanceof IDataHolder<?> dataHolder && dataHolder.cluelessCanReset()
					&& !dataHolder.getData(stack).equals(dataHolder.getDefaultData()))
			{
				dataHolder.removeData(stack);
				performed.set(true);
			}
		});

		if(performed.get())
		{
			user.playSound(AlchemancySoundEvents.CLUELESS.value());
			if(InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder()))
				InfusedPropertiesHelper.removeProperty(stack, asHolder());
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x91005E;
	}
}
