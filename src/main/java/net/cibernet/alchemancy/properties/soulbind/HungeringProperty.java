package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class HungeringProperty extends Property {

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(user.tickCount % 10 != 0 || user.level().isClientSide()) return;

		if(!eat(user, stack, false) &&
				(slot.isArmor() || InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY)) && user instanceof Player player)
		{
			Inventory inventory = player.getInventory();

			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.HOLLOW))
			{
				ItemStack storedStack = AlchemancyProperties.HOLLOW.get().getData(stack);
				if(!storedStack.isEmpty())
				{
					eat(user, storedStack, true);
					AlchemancyProperties.HOLLOW.get().setData(stack, storedStack);
				}
			}
			else for (int i = 0; i < inventory.items.size(); i++) {
				if(eat(user, inventory.getItem(i), true))
					break;
			}
		}

	}

	private boolean eat(LivingEntity user, ItemStack stack, boolean eatEfficiently) {
		var foodData = (user instanceof Player player) ? player.getFoodData() : null;
		var food = stack.getFoodProperties(user);

		if(foodData == null || (food != null && (food.canAlwaysEat() || (!eatEfficiently ? foodData.needsFood() :
				foodData.getFoodLevel() <= 0 || foodData.getFoodLevel() <= 20 - food.nutrition()
		))))
		{
			//TODO eating particles
			stack.finishUsingItem(user.level(), user);
			//InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onFinishUsingItem(user, user.level(), stack));
			return true;
		}
		return false;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xA53B64;
	}
}
