package net.cibernet.alchemancy.item;

import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.properties.data.modifiers.PropertyModifierType;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;

public class InnatePropertyItem extends Item
{
	public final int useTime;
	public final UseAnim useAnim;

	public static final ArrayList<Item> TOGGLEABLE_ITEMS = new ArrayList<>();

	private InnatePropertyItem(Properties properties, int useTime, UseAnim useAnim, boolean toggleable)
	{
		super(properties);
		this.useTime = useTime;
		this.useAnim = useAnim;

		if(toggleable)
			TOGGLEABLE_ITEMS.add(this);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return useAnim;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return useTime;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(useTime > 0)
		{
			ItemStack itemstack = player.getItemInHand(hand);
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(itemstack);
		}
		else return super.use(level, player, hand);
	}

	public static class Builder
	{
		private final ArrayList<Holder<Property>> properties = new ArrayList<>();
		private int useTime = 0;
		private UseAnim useAnim = UseAnim.NONE;
		private int stacksTo = 64;
		private int infusionSlots = -1;
		private final Map<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> modifiers = new HashMap<>();
		private final PropertyDataComponent propertyData = new PropertyDataComponent(new HashMap<>());

		@SafeVarargs
		public final Builder withProperties(Holder<Property>... properties)
		{
			this.properties.addAll(List.of(properties));
			return this;
		}

		public Builder use(int useTime, UseAnim useAnim)
		{
			this.useTime = useTime;
			this.useAnim = useAnim;
			return this;
		}

		public Builder infusionSlots(int slots)
		{
			this.infusionSlots = slots;
			return this;
		}

		public Builder stacksTo(int stack)
		{
			this.stacksTo = stack;
			return this;
		}

		public <T> Builder addModifier(Holder<Property> property, Holder<PropertyModifierType<T>> modifier, T value)
		{
			if(!modifiers.containsKey(property))
				modifiers.put(property, new HashMap<>());

			modifiers.get(property).put(AlchemancyProperties.Modifiers.asHolder(modifier.value()), value);
			return this;
		}

		public <T, P extends Property & IDataHolder<T>> Builder addData(DeferredHolder<Property, P> property, T value)
		{
			property.value().setData(propertyData, value);
			return this;
		}

		public Builder toggleable(boolean enabledByDefault)
		{
			withProperties(AlchemancyProperties.TOGGLEABLE);
			if(!enabledByDefault)
				addData(AlchemancyProperties.TOGGLEABLE, false);
			return this;
		}

		public InnatePropertyItem build()
		{
			return build(new Properties());
		}

		public InnatePropertyItem build(Properties itemProperties)
		{
			return new InnatePropertyItem(itemProperties
					.stacksTo(stacksTo)
					.component(AlchemancyItems.Components.INNATE_PROPERTIES, new InfusedPropertiesComponent(properties))
					.component(AlchemancyItems.Components.PROPERTY_MODIFIERS, new PropertyModifierComponent(modifiers))
					.component(AlchemancyItems.Components.PROPERTY_DATA, propertyData)
					.component(AlchemancyItems.Components.INFUSION_SLOTS, infusionSlots)
			, useTime, useAnim, properties.contains(AlchemancyProperties.TOGGLEABLE));
		}
	}
}
