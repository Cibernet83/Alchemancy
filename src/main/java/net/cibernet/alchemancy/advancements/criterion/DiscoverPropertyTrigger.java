package net.cibernet.alchemancy.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class DiscoverPropertyTrigger extends SimpleCriterionTrigger<DiscoverPropertyTrigger.TriggerInsance>
{
	@Override
	public Codec<TriggerInsance> codec() {
		return TriggerInsance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack stack)
	{
		super.trigger(player, triggerInsance -> triggerInsance.matches(stack));
	}

	public record TriggerInsance(Optional<ContextAwarePredicate> player, Optional<Holder<Property>> property) implements SimpleCriterionTrigger.SimpleInstance
	{
		public static final Codec<TriggerInsance> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInsance::player),
					Property.CODEC.optionalFieldOf("property").forGetter(TriggerInsance::property)
				).apply(instance, TriggerInsance::new)
		);

		public boolean matches(ItemStack stack)
		{
			return property.isEmpty() || InfusedPropertiesHelper.hasInfusedProperty(stack, property.get());
		}

		public boolean matches(Holder<Property> property)
		{
			return this.property.isEmpty() || this.property.get().is(Objects.requireNonNull(property.getKey()));
		}

		public static Criterion<TriggerInsance> discoverProperty(Holder<Property> propertyHolder)
		{
			return AlchemancyCriteriaTriggers.DISCOVER_PROPERTY.get().createCriterion(new TriggerInsance(Optional.empty(), Optional.of(propertyHolder)));
		}
	}
}
