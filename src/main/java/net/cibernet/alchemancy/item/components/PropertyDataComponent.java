package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record PropertyDataComponent(Map<Holder<Property>, CustomData> data)
{
	public static final PropertyDataComponent EMPTY = new PropertyDataComponent(Map.of());
	public static final Codec<PropertyDataComponent> CODEC = Codec.unboundedMap(Property.CODEC, CustomData.CODEC).xmap(PropertyDataComponent::new, PropertyDataComponent::data);
	public static final StreamCodec<RegistryFriendlyByteBuf, PropertyDataComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, Property.STREAM_CODEC, CustomData.STREAM_CODEC),
			PropertyDataComponent::data,
			PropertyDataComponent::new
	);

	public Optional<CompoundTag> getDataNbt(Holder<Property> property)
	{
		return data.containsKey(property) ? Optional.of(data.get(property).copyTag()) : Optional.empty();
	}

	/*
	public <T, P extends Property & IDataHolder<T>> Optional<T> getData(Holder<P> property)
	{
		return Optional.of(property.value().readData(data.get(property).copyTag()));
	}
	*/

	public static class Mutable
	{
		private final HashMap<Holder<Property>, CustomData> data;

		public Mutable(PropertyDataComponent data) {
			this.data = new HashMap<>(data.data);
		}

		public Optional<CompoundTag> getDataNbt(Holder<Property> property)
		{
			return data.containsKey(property) ? Optional.of(data.get(property).copyTag()) : Optional.empty();
		}

		public void setDataNbt(Holder<Property> property, CompoundTag value)
		{
			data.put(property, CustomData.of(value));
		}

		public PropertyDataComponent toImmutable()
		{
			return new PropertyDataComponent(data);
		}
	}

	public static class Jim
	{

	}
}
