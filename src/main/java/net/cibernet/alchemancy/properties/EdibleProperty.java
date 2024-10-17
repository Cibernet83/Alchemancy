package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class EdibleProperty extends Property
{
	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.FOOD)
		{
			if(data == null)
				return new FoodProperties(2, 0.5f, true, 1.6f, Optional.empty(), List.of());
			else if(data instanceof FoodProperties foodProperties)
				return new FoodProperties((int) (foodProperties.nutrition() * 1.5f), foodProperties.saturation() * 1.25f, true, foodProperties.eatSeconds(), foodProperties.usingConvertsTo(), foodProperties.effects());
		}
		return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack)
	{
		if(stack.isDamageableItem() || PropertyModifierComponent.get(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION))
		{
			user.eat(level, stack.copy());
			stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 10), user, EquipmentSlot.MAINHAND);
			return true;
		}
		return false;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF7777;
	}
}
