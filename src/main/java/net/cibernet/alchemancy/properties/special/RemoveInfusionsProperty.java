package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class RemoveInfusionsProperty extends Property {

	private final Supplier<Integer> color;
	private final TagKey<Property> toRemove;

	public RemoveInfusionsProperty(Supplier<Integer> color) {
		this(color, null);
	}

	public RemoveInfusionsProperty(Supplier<Integer> color, TagKey<Property> toRemove) {
		this.color = color;
		this.toRemove = toRemove;
	}

	@Override
	public void onStackedOverItem(ItemStack stack, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled) {
		if (clickAction == ClickAction.SECONDARY && !stackedOnItem.isEmpty()) {

			if(toRemove == null)
				InfusedPropertiesHelper.clearAllInfusions(stackedOnItem);
			else InfusedPropertiesHelper.getInfusedProperties(stack).stream().filter(propertyHolder -> propertyHolder.is(toRemove)).forEach(propertyHolder ->
					InfusedPropertiesHelper.removeProperty(stack, propertyHolder));
			isCancelled.set(true);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack armor) {

		if(slot.isArmor() && toRemove != null && user.getRandom().nextFloat() < 0.05f)
		{
			ItemStack stack;
			if(user instanceof Player player)
				stack = player.getInventory().items.get(user.getRandom().nextInt(player.getInventory().items.size()));
			else stack = user.getItemBySlot(EquipmentSlot.values()[user.getRandom().nextInt(EquipmentSlot.values().length)]);

			if(!stack.isEmpty()) {
				InfusedPropertiesHelper.getInfusedProperties(stack).stream().filter(propertyHolder -> propertyHolder.is(toRemove)).forEach(propertyHolder ->
						InfusedPropertiesHelper.removeProperty(stack, propertyHolder));
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return color.get();
	}
}
