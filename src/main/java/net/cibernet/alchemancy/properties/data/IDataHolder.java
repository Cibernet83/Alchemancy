package net.cibernet.alchemancy.properties.data;

import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface IDataHolder<T>
{
	T readData(CompoundTag tag);
	CompoundTag writeData(T data);

	T getDefaultData();

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
}
