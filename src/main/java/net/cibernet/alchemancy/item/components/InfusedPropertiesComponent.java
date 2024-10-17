package net.cibernet.alchemancy.item.components;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public record InfusedPropertiesComponent(List<Holder<Property>> properties)
{
	//Stores Properties alongside their data and augments

	public static final Codec<InfusedPropertiesComponent> CODEC = Codec.list(Property.CODEC).xmap(InfusedPropertiesComponent::new, (a) -> a.properties);

	public static final StreamCodec<RegistryFriendlyByteBuf, InfusedPropertiesComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.holderRegistry(AlchemancyProperties.REGISTRY.getRegistryKey()).apply(ByteBufCodecs.list()),
			comp -> comp.properties,
			InfusedPropertiesComponent::new
	);
	public static final InfusedPropertiesComponent EMPTY = new InfusedPropertiesComponent(new ArrayList<>());

	public void forEachProperty(Consumer<Holder<Property>> consumer) {
		properties.stream().sorted(Comparator.comparingInt(p -> p.value().getPriority())).forEach(consumer);
	}

	public boolean hasProperty(Holder<Property> property) {
		return properties.contains(property);
		/*
		for (Holder<Property> propertyHolder : properties)
			if(propertyHolder.is(property.getKey()))
				return true;
		return false;
		*/
	}

	@Override
	public boolean equals(Object other) {
		return other == this || (other instanceof InfusedPropertiesComponent itemenchantments && this.properties.equals(itemenchantments.properties));
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("[");
		for (Holder<Property> holder : properties) {
			str.append(holder.value().getKey()).append(" ");
		}
		str.append("]");

		return str.toString();
	}

	public static class Mutable {
		private final List<Holder<Property>> properties;

		public Mutable(InfusedPropertiesComponent component) {

			this.properties = new ArrayList<>(component.properties);
		}

		public boolean hasProperty(Holder<Property> property) {
			return properties.contains(property);
		}

		public boolean addProperty(Holder<Property> property) {

			if (!hasProperty(property))
				return properties.add(property);
			return false;
		}

		public boolean removeProperty(Holder<Property> property) {
			return properties.remove(property);
		}

		public boolean truncateProperties(int limit) {
			if (properties.size() <= limit)
				return false;
			properties.subList(limit, properties.size()).clear();
			return true;
		}

		public InfusedPropertiesComponent toImutable() {
			return new InfusedPropertiesComponent(properties);
		}
	}
}
