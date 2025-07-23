package net.cibernet.alchemancy.item.components;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.cibernet.alchemancy.registries.AlchemancyItems.Components.*;

public class InfusedPropertiesHelper
{
	public static boolean hasItemWithProperty(LivingEntity user, Holder<Property> property, boolean checkAuxiliary)
	{
		return hasItemWithProperty(user, property, checkAuxiliary, EquipmentSlotGroup.ANY);
	}
	public static boolean hasItemWithProperty(LivingEntity user, Holder<Property> property, boolean checkAuxiliary, EquipmentSlotGroup equipmentSlots)
	{
		if(checkAuxiliary && user instanceof Player player)
		{
			Inventory inventory = player.getInventory();
			List<Holder<Property>> propertiesToCheck = List.of(AlchemancyProperties.AUXILIARY, property);

			for(int slot = 0; slot < inventory.getContainerSize(); slot++)
			{
				ItemStack stack = inventory.getItem(slot);
				if(InfusedPropertiesHelper.hasProperties(stack, propertiesToCheck, true))
				{
					return true;
				}
			}
		}

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if(!equipmentSlots.test(slot))
				continue;
			ItemStack stack = user.getItemBySlot(slot);
			if(InfusedPropertiesHelper.hasProperty(stack, property))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasProperty(ItemStack stack, Holder<Property> property)
	{
		if(!property.isBound() || stack == null || stack.isEmpty()|| property == null || property.is(AlchemancyTags.Properties.DISABLED))
			return false;

		boolean toggled = AlchemancyProperties.TOGGLEABLE.get().getData(stack);


		return ((toggled || !hasInfusedProperty(stack, AlchemancyProperties.TOGGLEABLE)) && hasInfusedProperty(stack, property))
			|| ((toggled || !hasInnateProperty(stack, AlchemancyProperties.TOGGLEABLE)) && (property == AlchemancyProperties.DEAD || !hasProperty(stack, AlchemancyProperties.DEAD)) && hasInnateProperty(stack, property))
			|| (hasInfusedProperty(stack, AlchemancyProperties.AWAKENED) && hasDormantProperty(stack, property));
	}

	public static boolean hasProperty(ItemStack stack, TagKey<Property> propertyTag)
	{
		if(stack == null || stack.isEmpty() || propertyTag == null)
			return false;

		boolean toggled = AlchemancyProperties.TOGGLEABLE.get().getData(stack);

		return ((toggled || !hasInfusedProperty(stack, AlchemancyProperties.TOGGLEABLE)) && hasInfusedProperty(stack, propertyTag))
			|| ((toggled || !hasInnateProperty(stack, AlchemancyProperties.TOGGLEABLE)) && hasInnateProperty(stack, propertyTag))
			|| ((hasProperty(stack, AlchemancyProperties.AWAKENED)) && hasDormantProperty(stack, propertyTag));
	}

	public static boolean hasProperties(ItemStack stack, List<Holder<Property>> properties, boolean matchesAll)
	{
		if(stack == null || stack.isEmpty())
			return false;

		for (Holder<Property> property : properties)
		{
			if(hasProperty(stack, property) != matchesAll)
				return !matchesAll;
		}

		return matchesAll;
	}

	public static boolean hasInfusedProperty(ItemStack stack, Holder<Property> property)
	{
		return stack != null && !stack.isEmpty() && INFUSED_PROPERTIES.isBound() &&
				!stack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES)  && stack.has(INFUSED_PROPERTIES.get()) && stack.get(INFUSED_PROPERTIES.get()).hasProperty(property);
	}

	public static boolean hasInnateProperty(ItemStack stack, Holder<Property> property)
	{
		return stack != null && INNATE_PROPERTIES.isBound() &&
				!stack.isEmpty() && stack.has(INNATE_PROPERTIES.get()) && stack.get(INNATE_PROPERTIES.get()).hasProperty(property);
	}

	public static boolean hasDormantProperty(ItemStack stack, Holder<Property> property)
	{
		return stack != null && !stack.isEmpty() && AlchemancyProperties.getDormantProperties(stack).contains(property);
	}

	public static boolean hasInfusedProperty(ItemStack stack, TagKey<Property> propertyTag)
	{
		return stack != null && !stack.isEmpty() && INFUSED_PROPERTIES.isBound() &&
				!stack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES) && stack.has(INFUSED_PROPERTIES.get()) && stack.get(INFUSED_PROPERTIES.get()).hasProperty(propertyTag);
	}

	public static boolean hasInnateProperty(ItemStack stack, TagKey<Property> propertyTag)
	{
		return stack != null && !stack.isEmpty() && INNATE_PROPERTIES.isBound() &&
				stack.has(INNATE_PROPERTIES.get()) && stack.get(INNATE_PROPERTIES.get()).hasProperty(propertyTag);
	}

	public static boolean hasDormantProperty(ItemStack stack, TagKey<Property> propertyTag)
	{
		return stack != null &&  !stack.isEmpty() && AlchemancyProperties.getDormantProperties(stack).stream().anyMatch(property -> property.is(propertyTag));
	}

	public static boolean modifyInfusions(ItemStack stack, Function<InfusedPropertiesComponent.Mutable, Boolean> consumer)
	{
		InfusedPropertiesComponent.Mutable mutable = new InfusedPropertiesComponent.Mutable(stack.getOrDefault(INFUSED_PROPERTIES.get(), InfusedPropertiesComponent.EMPTY));
		boolean result = consumer.apply(mutable);
		stack.set(INFUSED_PROPERTIES.get(), mutable.toImutable());
		return result;
	}

	public static void forEachProperty(ItemStack stack, Consumer<Holder<Property>> consumer)
	{
		forEachProperty(stack, consumer, true);
	}

	public static void forEachProperty(ItemStack stack, Consumer<Holder<Property>> consumer, boolean checkToggle)
	{
		if(stack == null || stack.isEmpty())
			return;

		boolean toggled = !checkToggle || (AlchemancyProperties.TOGGLEABLE.isBound() && AlchemancyProperties.TOGGLEABLE.get().getData(stack));

		if (INFUSED_PROPERTIES.isBound() && stack.has(INFUSED_PROPERTIES.get()) && !stack.is(AlchemancyTags.Items.DISABLES_INFUSION_ABILITIES))
		{
			if(!toggled && hasInfusedProperty(stack, AlchemancyProperties.TOGGLEABLE))
				consumer.accept(AlchemancyProperties.TOGGLEABLE);
			else stack.get(INFUSED_PROPERTIES.get()).forEachProperty(consumer);
		}
		if (INNATE_PROPERTIES.isBound() && !hasProperty(stack, AlchemancyProperties.DEAD) && stack.has(INNATE_PROPERTIES.get()))
		{
			if(!toggled && hasInnateProperty(stack, AlchemancyProperties.TOGGLEABLE))
				consumer.accept(AlchemancyProperties.TOGGLEABLE);
			else stack.get(INNATE_PROPERTIES.get()).forEachProperty(consumer);
		}
		if(hasProperty(stack, AlchemancyProperties.AWAKENED))
			AlchemancyProperties.getDormantProperties(stack).forEach(consumer);
	}

	public static void forEachInnateProperty(ItemStack stack, Consumer<Holder<Property>> consumer)
	{
		if(!INNATE_PROPERTIES.isBound() || stack == null || stack.isEmpty())
			return;

		boolean toggled = AlchemancyProperties.TOGGLEABLE.get().getData(stack);

		if (stack.has(INNATE_PROPERTIES.get()))
		{
			if(!toggled && hasInnateProperty(stack, AlchemancyProperties.TOGGLEABLE))
				consumer.accept(AlchemancyProperties.TOGGLEABLE);
			else stack.get(INNATE_PROPERTIES.get()).forEachProperty(consumer);
		}
		if(hasInnateProperty(stack, AlchemancyProperties.AWAKENED))
			AlchemancyProperties.getDormantProperties(stack).forEach(consumer);
	}

	public static ItemStack addProperty(ItemStack stack, Holder<Property> property)
	{
		modifyInfusions(stack, mutable -> mutable.addProperty(property));
		return stack;
	}

	public static ItemStack addProperties(ItemStack stack, List<Holder<Property>> properties)
	{
		modifyInfusions(stack, mutable ->
		{
			properties.forEach(mutable::addProperty);
			return !properties.isEmpty();
		});
		return stack;
	}

	public static ItemStack removeProperty(ItemStack stack, Holder<Property> property)
	{
		if(stack.has(INFUSED_PROPERTIES))
		{
			modifyInfusions(stack, mutable -> mutable.removeProperty(property));

			if(property.value() instanceof IDataHolder<?> dataHolder)
				dataHolder.removeData(stack);

			if (getInfusedProperties(stack).isEmpty())
				stack.remove(INFUSED_PROPERTIES);
		}
		return stack;
	}

	public static final int DEFAULT_INFUSION_SLOTS = 4;

	public static int getInfusionSlots(ItemStack stack)
	{
		return Math.max(0, stack.getOrDefault(AlchemancyItems.Components.INFUSION_SLOTS.get(), DEFAULT_INFUSION_SLOTS));
	}

	public static ItemStack truncateProperties(ItemStack stack)
	{
		if(stack.has(INFUSED_PROPERTIES))
			while(true)
			{
				truncateProperties(stack, getInfusionSlots(stack));
				if(getRemainingInfusionSlots(stack) >= 0)
					return stack;
			}
		return stack;
	}

	public static int getRemainingInfusionSlots(ItemStack stack)
	{
		List<Holder<Property>> infusedProperties = getInfusedProperties(stack);
		return getInfusionSlots(stack) - (infusedProperties.size() - (int) infusedProperties.stream().filter(propertyHolder -> propertyHolder.is(AlchemancyTags.Properties.SLOTLESS)).count());
	}

	public static ItemStack truncateProperties(ItemStack stack, int limit)
	{
		if(limit <= 0)
			stack.remove(INFUSED_PROPERTIES);
		else {
			final int finalLimit = limit;
			modifyInfusions(stack, mutable -> mutable.truncateProperties(finalLimit));
		}
		return stack;
	}

	public static ItemStack clearAllInfusions(ItemStack stack)
	{
		stack.remove(INFUSED_PROPERTIES);
		stack.remove(PROPERTY_DATA);
		return stack;
	}

	public static List<Holder<Property>> getInfusedProperties(ItemStack stack) {
		if(!INFUSED_PROPERTIES.isBound())
			return InfusedPropertiesComponent.EMPTY.properties();
		return stack.getOrDefault(INFUSED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties();
	}

	public static List<Holder<Property>> getInnateProperties(ItemStack stack) {
		if(!INNATE_PROPERTIES.isBound())
			return InfusedPropertiesComponent.EMPTY.properties();
		return stack.getOrDefault(INNATE_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties();
	}

	public static List<Holder<Property>> getStoredProperties(ItemStack stack) {
		if(!STORED_PROPERTIES.isBound())
			return InfusedPropertiesComponent.EMPTY.properties();
		return stack.getOrDefault(STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties();
	}

	public static ItemStack storeProperties(ItemStack stack, List<Holder<Property>> properties)
	{
		stack.set(AlchemancyItems.Components.STORED_PROPERTIES, new InfusedPropertiesComponent(properties));
		return stack;
	}
	@SafeVarargs
	public static ItemStack storeProperties(ItemStack stack, Holder<Property>... properties)
	{
		return storeProperties(stack, Arrays.asList(properties));
	}

	public static ItemStack createPropertyCapsule(List<Holder<Property>> properties)
	{
		return storeProperties(AlchemancyItems.PROPERTY_CAPSULE.toStack(), properties);
	}

	@SafeVarargs
	public static ItemStack createPropertyCapsule(Holder<Property>... properties)
	{
		return storeProperties(AlchemancyItems.PROPERTY_CAPSULE.toStack(), properties);
	}


	public static ItemStack createPropertyIngredient(List<Holder<Property>> properties)
	{
		ItemStack result = createPropertyCapsule(properties);
		result.set(AlchemancyItems.Components.INGREDIENT_DISPLAY, Unit.INSTANCE);
		return result;
	}

	@SafeVarargs
	public static ItemStack createPropertyIngredient(Holder<Property>... properties)
	{
		ItemStack result = createPropertyCapsule(properties);
		result.set(AlchemancyItems.Components.INGREDIENT_DISPLAY, Unit.INSTANCE);
		return result;
	}
}
