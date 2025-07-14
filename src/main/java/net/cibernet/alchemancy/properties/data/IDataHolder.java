package net.cibernet.alchemancy.properties.data;

import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IDataHolder<T>
{
	T readData(CompoundTag tag);
	CompoundTag writeData(T data);

	T getDefaultData();

	default T combineData(@Nullable T currentData, T newData) {
		return currentData == null ? newData : currentData;
	}

	default void combineDataAndSet(ItemStack stack, ItemStack from) {
		setData(stack, hasData(stack) ? combineData(getData(stack), getData(from)) : getData(from));
	}

	default boolean hasData(ItemStack stack) {
		return stack.has(AlchemancyItems.Components.PROPERTY_DATA) && (this instanceof Property property) && stack.get(AlchemancyItems.Components.PROPERTY_DATA).getDataNbt(property.asHolder()).isPresent();
	}

	default T getData(ItemStack stack)
	{
		if(this instanceof Property property)
		{
			if(stack.has(AlchemancyItems.Components.PROPERTY_DATA))
			{
				Optional<CompoundTag> nbt = stack.get(AlchemancyItems.Components.PROPERTY_DATA).getDataNbt(AlchemancyProperties.getHolder(property));
				if(nbt.isPresent())
					return readData(nbt.get());
			}
			return getDefaultData();
		}
		return getDefaultData();
	}

	default void setData(ItemStack stack, T value)
	{
		stack.set(AlchemancyItems.Components.PROPERTY_DATA, setData(stack.getOrDefault(AlchemancyItems.Components.PROPERTY_DATA, PropertyDataComponent.EMPTY), value));
	}

	default void removeData(ItemStack stack)
	{
		PropertyDataComponent comp = stack.getOrDefault(AlchemancyItems.Components.PROPERTY_DATA, PropertyDataComponent.EMPTY);
		comp = removeData(comp);
		if(comp.isEmpty())
			stack.remove(AlchemancyItems.Components.PROPERTY_DATA);
		else stack.set(AlchemancyItems.Components.PROPERTY_DATA, comp);
	}

	default PropertyDataComponent setData(PropertyDataComponent component, T value)
	{
		if(this instanceof Property property)
		{
			PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(component);
			data.setDataNbt(AlchemancyProperties.getHolder(property), writeData(value));
			return data.toImmutable();
		}
		return component;
	}

	default PropertyDataComponent removeData(PropertyDataComponent component)
	{
		if(this instanceof Property property)
		{
			PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(component);
			data.removeData(AlchemancyProperties.getHolder(property));
			return data.toImmutable();
		}

		return component;
	}

	default boolean cluelessCanReset()
	{
		return true;
	}

	default void copyData(ItemStack from, ItemStack to)
	{
		setData(to, getData(from));
	};
}
