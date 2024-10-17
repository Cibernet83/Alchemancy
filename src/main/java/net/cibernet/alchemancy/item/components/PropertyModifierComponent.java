package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.modifiers.PropertyModifierType;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;

public record PropertyModifierComponent(Map<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> map)
{

	public static Codec<Map<Holder<PropertyModifierType<?>>, Object>> MAP_CODEC = Codec.dispatchedMap(PropertyModifierType.CODEC, type -> type.value().codec());
	public static StreamCodec<RegistryFriendlyByteBuf, Map<Holder<PropertyModifierType<?>>, Object>> MAP_STREAM_CODEC = StreamCodec.of((encode, map) ->
			{
				encode.writeInt(map.size());

				for (Map.Entry<Holder<PropertyModifierType<?>>, Object> entry : map.entrySet()) {
					PropertyModifierType.STREAM_CODEC.encode(encode, entry.getKey());
					entry.getKey().value().encode(encode, entry.getValue());
				}
			},
			decode ->
			{
				int size = decode.readInt();
				HashMap<Holder<PropertyModifierType<?>>, Object> map = new HashMap<>();
				for (int i = 0; i < size; i++)
				{
					Holder<PropertyModifierType<?>> key = PropertyModifierType.STREAM_CODEC.decode(decode);
					map.put(key, key.value().streamCodec().decode(decode));
				}

				return map;
			});

	public static Codec<PropertyModifierComponent> CODEC = Codec.unboundedMap(Property.CODEC, MAP_CODEC).xmap(PropertyModifierComponent::new, PropertyModifierComponent::map);
	public static StreamCodec<RegistryFriendlyByteBuf, PropertyModifierComponent> STREAM_CODEC = StreamCodec.of((encode, comp) ->
			{
				encode.writeInt(comp.map.size());

				for (Map.Entry<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> entry : comp.map.entrySet()) {
					Property.STREAM_CODEC.encode(encode, entry.getKey());
					MAP_STREAM_CODEC.encode(encode, entry.getValue());
				}
			},
			decode ->
			{
				int size = decode.readInt();
				HashMap<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> map = new HashMap<>();
				for (int i = 0; i < size; i++)
				{
					map.put(Property.STREAM_CODEC.decode(decode), MAP_STREAM_CODEC.decode(decode));
				}

				return new PropertyModifierComponent(map);
			});

	public <T> T get(Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> type)
	{
		return getOrElse(property, type, type.value().defaultValue());
	}

	public <T> T getOrElse(Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> type, T defaultValue)
	{
		return map.containsKey (property) ? (T) map.get(property).getOrDefault(type, defaultValue) : defaultValue;
	}

	public static <T> T get(ItemStack stack, Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> type)
	{
		return getOrElse(stack, property, type, type.value().defaultValue());
	}

	public static <T> T getOrElse(ItemStack stack, Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> type, T defaultValue)
	{
		return stack.has(AlchemancyItems.Components.PROPERTY_MODIFIERS) ? stack.get(AlchemancyItems.Components.PROPERTY_MODIFIERS).getOrElse(property, type, defaultValue) : defaultValue;
	}
}
