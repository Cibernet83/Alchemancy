package net.cibernet.alchemancy.properties.entangled;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

public abstract class AbstractEntangledProperty extends Property implements IDataHolder<AbstractEntangledProperty.EntangledData>
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xE8FF00;
	}

	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		EntangledData data = getData(stackedOnItem);
		if(clickAction != ClickAction.SECONDARY || carriedItem.isEmpty() || !data.equals(getDefaultData()) || InfusedPropertiesHelper.getRemainingInfusionSlots(carriedItem) <= 0)
			return;

		InfusedPropertiesHelper.addProperty(carriedItem, asHolder());
		setStoredItem(stackedOnItem, carriedItem.split(1));

		event.setCanceled(true);
	}

	public boolean getToggle(ItemStack stack)
	{
		return getData(stack).toggled();
	}

	public ItemStack getStoredItem(ItemStack stack)
	{
		return getData(stack).stack;
	}

	public void setToggle(ItemStack stack, boolean value)
	{
		setData(stack, new EntangledData(getData(stack).stack, value));
	}

	public void setData(ItemStack stack, ItemStack storedStack, boolean toggled)
	{
		setData(stack, new EntangledData(storedStack, toggled));
	}

	public void setStoredItem(ItemStack stack, ItemStack value)
	{
		setData(stack, new EntangledData(value, getData(stack).toggled));
	}

	public ItemStack shift(ItemStack stack)
	{
		EntangledData data = getData(stack);
		ItemStack storedItem = data.stack;
		if(storedItem.equals(getDefaultData().stack))
			return stack;

		setStoredItem(stack, getDefaultData().stack);
		setData(storedItem, stack, data.toggled);
		return storedItem;
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
	}

	@Override
	public EntangledData readData(CompoundTag tag)
	{
		return !tag.contains("item") ? getDefaultData() : new EntangledData(ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(getDefaultData().stack()), tag.getBoolean("toggled"));
	}

	@Override
	public CompoundTag writeData(EntangledData data) {
		return new CompoundTag() {{
			if(!data.stack.isEmpty())
				put("item", data.stack.save(CommonUtils.registryAccessStatic()));
			putBoolean("toggled", data.toggled);
		}};
	}

	@Override
	public EntangledData getDefaultData() {
		return EntangledData.DEFAULT;
	}

	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		ItemStack storedStack = getData(stack).stack;

		if(!storedStack.isEmpty())
			return Component.translatable("property.detail", name, Component.translatable("property.detail.item_count", storedStack.getHoverName(), storedStack.getCount())).withColor(getColor(stack));
		return name;
	}

	public record EntangledData(ItemStack stack, boolean toggled)
	{
		public static final EntangledData DEFAULT = new EntangledData(ItemStack.EMPTY, false);

		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof EntangledData data))
				return false;
			return stack.equals(data.stack);
		}
	}
}
