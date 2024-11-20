package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractTimerProperty extends Property implements IDataHolder<Long>
{
	public void resetStartTimestamp(ItemStack stack)
	{
		if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.getHolder(this)))
			setData(stack, CommonUtils.getLevelData().getDayTime());
	}

	public long getElapsedTime(ItemStack stack)
	{

		long timestamp = getData(stack);
		return timestamp == 0 ? 0 : CommonUtils.getLevelData().getDayTime() - timestamp;
	}

	@Override
	public Long readData(CompoundTag tag) {
		return tag.getLong("starting_timestamp");
	}

	@Override
	public CompoundTag writeData(Long data) {
		return new CompoundTag(){{putLong("starting_timestamp", data);}};
	}

	@Override
	public Long getDefaultData() {
		return 0L;
	}
}
