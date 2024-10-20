package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.entity.ai.TemptByRootedGoal;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@EventBusSubscriber
public class MobTemptHandler
{

	private static final HashMap<Holder<Property>, Predicate<Entity>> PROPERTY_MAP = new HashMap<>();

	static {
		registerTemptProperty(AlchemancyProperties.SWEET, AlchemancyTags.EntityTypes.TEMPTED_BY_SWEET);
		registerTemptProperty(AlchemancyProperties.WEALTHY, AlchemancyTags.EntityTypes.TEMPTED_BY_WEALTHY);
		registerTemptProperty(AlchemancyProperties.SEEDED, AlchemancyTags.EntityTypes.AGGROED_BY_SEEDED);
	}

	public static void registerTemptProperty(Holder<Property> property, Predicate<Entity> entityPredicate)
	{
		PROPERTY_MAP.put(property, entityPredicate);
	}

	public static void registerTemptProperty(Holder<Property> property, TagKey<EntityType<?>> entityTag)
	{
		registerTemptProperty(property, entity -> entity.getType().is(entityTag));
	}

	public static void performIfTempted(Entity entity, ItemStack stack, Action action)
	{
		for (Map.Entry<Holder<Property>, Predicate<Entity>> entry : PROPERTY_MAP.entrySet())
		{
			if(InfusedPropertiesHelper.hasInfusedProperty(stack, entry.getKey()) && entry.getValue().test(entity))
			{
				action.perform();
				return;
			}
		}
	}

	public static void performIfTempted(Entity entity, LivingEntity user, EquipmentSlotGroup slots, Action action)
	{

		for(EquipmentSlot slot : EquipmentSlot.values())
		{
			if(slots.test(slot))
			{
				ItemStack stack = user.getItemBySlot(slot);
				for (Map.Entry<Holder<Property>, Predicate<Entity>> entry : PROPERTY_MAP.entrySet()) {
					if (InfusedPropertiesHelper.hasInfusedProperty(stack, entry.getKey()) && entry.getValue().test(entity)) {
						action.perform();
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event)
	{
		if(event.getEntity() instanceof PathfinderMob mob)
		{
			PROPERTY_MAP.forEach(((propertyHolder, entityPredicate) ->
			{
				if(entityPredicate.test(mob))
				{
					mob.goalSelector.addGoal(3, new TemptGoal(mob, 1.25, stack -> InfusedPropertiesHelper.hasProperty(stack, propertyHolder), false));
					mob.goalSelector.addGoal(0, new TemptByRootedGoal(mob, 1.25, propertyHolder));
				}
			}));
		}

	}

	public interface Action
	{
		void perform();
	}
}
