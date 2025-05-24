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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnatePropertyItem extends Item
{
	public final int useTime;
	public final UseAnim useAnim;
	@Nullable
	public final Tooltip tooltip;
	public final Ingredient repairMaterial;

	public static final ArrayList<Item> TOGGLEABLE_ITEMS = new ArrayList<>();

	private InnatePropertyItem(Properties properties, int useTime, UseAnim useAnim, boolean toggleable, @Nullable Tooltip tooltip, Ingredient repairMaterial)
	{
		super(properties);
		this.useTime = useTime;
		this.useAnim = useAnim;
		this.tooltip = tooltip;
		this.repairMaterial = repairMaterial;

		if(toggleable)
			TOGGLEABLE_ITEMS.add(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(tooltip != null)
			tooltip.apply(stack, context, tooltipComponents, tooltipFlag);
	}

	public interface Tooltip
	{
		void apply(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag);
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

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return super.isValidRepairItem(stack, repairCandidate);
	}

	public static class Builder
	{
		private final ArrayList<Holder<Property>> properties = new ArrayList<>();
		private int useTime = 0;
		private UseAnim useAnim = UseAnim.NONE;
		private int stacksTo = 64;
		private int infusionSlots = -1;
		private int durability = -1;
		private Tooltip tooltip = null;
		private Ingredient repairMaterial = Ingredient.EMPTY;

		private final Map<Holder<Property>, Map<Holder<PropertyModifierType<?>>, Object>> modifiers = new HashMap<>();
		private PropertyDataComponent propertyData = new PropertyDataComponent(new HashMap<>());

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

		public Builder durability(int durability)
		{
			this.durability = durability;
			return this;
		}

		public Builder durability(int durability, Ingredient repairMaterial)
		{
			this.repairMaterial = repairMaterial;
			return durability(durability);
		}

		public Builder tooltip(Tooltip tooltip)
		{
			this.tooltip = tooltip;
			return this;
		}

		public <T> Builder addModifier(Holder<Property> property, DeferredHolder<PropertyModifierType<?>, PropertyModifierType<T>> modifier, T value)
		{
			if(!modifiers.containsKey(property))
				modifiers.put(property, new HashMap<>());

			modifiers.get(property).put(modifier, value);
			return this;
		}

		public <T, P extends Property & IDataHolder<T>> Builder addData(DeferredHolder<Property, P> property, CompoundTag value)
		{
			PropertyDataComponent.Mutable data = new PropertyDataComponent.Mutable(propertyData);
			data.setDataNbt(property, value);
			propertyData = data.toImmutable();
			return this;
		}

		public Builder toggleable(boolean enabledByDefault)
		{
			withProperties(AlchemancyProperties.TOGGLEABLE);
			if(!enabledByDefault)
				addData(AlchemancyProperties.TOGGLEABLE, new CompoundTag(){{putBoolean("active", false);}});
			return this;
		}

		public Builder auxiliary(boolean ignoreInfused)
		{
			withProperties(AlchemancyProperties.AUXILIARY);
			if(ignoreInfused)
				addModifier(AlchemancyProperties.AUXILIARY, AlchemancyProperties.Modifiers.IGNORE_INFUSED, true);
			return this;
		}

		public InnatePropertyItem build()
		{
			return build(new Properties());
		}

		public InnatePropertyItem build(Properties itemProperties)
		{
			if(infusionSlots >= 0)
				itemProperties.component(AlchemancyItems.Components.INFUSION_SLOTS, infusionSlots);
			if(durability >= 0)
				itemProperties.durability(durability);
			return new InnatePropertyItem(itemProperties
					.stacksTo(stacksTo)
					.component(AlchemancyItems.Components.INNATE_PROPERTIES, new InfusedPropertiesComponent(properties))
					.component(AlchemancyItems.Components.PROPERTY_MODIFIERS, new PropertyModifierComponent(modifiers))
					.component(AlchemancyItems.Components.PROPERTY_DATA, propertyData)
			, useTime, useAnim, properties.contains(AlchemancyProperties.TOGGLEABLE), tooltip, repairMaterial);
		}
	}
}
