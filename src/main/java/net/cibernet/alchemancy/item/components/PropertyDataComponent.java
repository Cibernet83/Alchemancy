package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record PropertyDataComponent(Map<Holder<Property>, CompoundTag> data)
{
	public static final PropertyDataComponent EMPTY = new PropertyDataComponent(Map.of());
	public static final Codec<PropertyDataComponent> CODEC = Codec.unboundedMap(Property.CODEC, CompoundTag.CODEC).xmap(PropertyDataComponent::new, PropertyDataComponent::data);
	public static final StreamCodec<RegistryFriendlyByteBuf, PropertyDataComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, Property.STREAM_CODEC, ByteBufCodecs.COMPOUND_TAG),
			PropertyDataComponent::data,
			PropertyDataComponent::new
	);

	public Optional<CompoundTag> getDataNbt(Holder<Property> property)
	{
		return data.containsKey(property) ? Optional.of(data.get(property)) : Optional.empty();
	}

	public static void mergeData(ItemStack to, ItemStack from)
	{
		var toData = to.get(AlchemancyItems.Components.PROPERTY_DATA);
		var fromData = from.get(AlchemancyItems.Components.PROPERTY_DATA);

		if(fromData == null)
			return;
		if(toData == null)
			to.set(AlchemancyItems.Components.PROPERTY_DATA, fromData);
		else to.set(AlchemancyItems.Components.PROPERTY_DATA, new Mutable(toData).mergeData(fromData).toImmutable());
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	/*
	public <T, P extends Property & IDataHolder<T>> Optional<T> getData(Holder<P> property)
	{
		return Optional.of(property.value().readData(data.get(property).copyTag()));
	}
	*/

	public static class Mutable
	{
		private final HashMap<Holder<Property>, CompoundTag> data;

		public Mutable(PropertyDataComponent data) {
			this.data = new HashMap<>(data.data);
		}

		public Optional<CompoundTag> getDataNbt(Holder<Property> property)
		{
			return data.containsKey(property) ? Optional.of(data.get(property)) : Optional.empty();
		}

		public void setDataNbt(Holder<Property> property, CompoundTag value)
		{
			data.put(property, value);
		}

		public void removeData(Holder<Property> propertyHolder)
		{
			data.remove(propertyHolder);
		}

		public PropertyDataComponent toImmutable()
		{
			return new PropertyDataComponent(data);
		}

		public Mutable mergeData(PropertyDataComponent other) {
			for (Map.Entry<Holder<Property>, CompoundTag> entry : other.data().entrySet()) {

				if(data.containsKey(entry.getKey()))
					data.get(entry.getKey()).merge(entry.getValue());
				else data.put(entry.getKey(), entry.getValue());
			}
			return this;
		}
	}

	public static class Jim
	{

	}
}
