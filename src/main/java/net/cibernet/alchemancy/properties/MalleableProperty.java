package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MalleableProperty extends Property
{
	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		if(stack.isDamageableItem())
			stack.setDamageValue(stack.getMaxDamage() - 1);
		ItemStack clayWad = AlchemancyItems.UNSHAPED_CLAY.toStack();
		AlchemancyProperties.CLAY_MOLD.get().setData(clayWad, stack.copy());

		itemEntity.level().addFreshEntity(new ItemEntity(itemEntity.level(), itemEntity.position().x, itemEntity.position().y, itemEntity.position().z, clayWad));
		stack.setCount(0);
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{

		if(stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount)
		{
			stack.setDamageValue(stack.getMaxDamage() - 1);

			ItemStack clayWad = AlchemancyItems.UNSHAPED_CLAY.toStack();
			AlchemancyProperties.CLAY_MOLD.get().setData(clayWad, stack.copy());

			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
			{
				if(propertyHolder.is(AlchemancyTags.Properties.RETAINED_BY_UNSHAPED_CLAY))
				{
					InfusedPropertiesHelper.addProperty(clayWad, propertyHolder);
					if(propertyHolder.value() instanceof IDataHolder<?> dataHolder)
						dataHolder.copyData(stack, clayWad);
				}
			});

			if(user.getMainHandItem() == stack)
				user.setItemInHand(InteractionHand.MAIN_HAND, clayWad);
			else if(user.getOffhandItem() == stack)
				user.setItemInHand(InteractionHand.OFF_HAND, clayWad);
			else if(user instanceof Player player)
			{
				if(!player.addItem(clayWad))
					player.drop(clayWad, true);
			}
			else HollowProperty.nonPlayerDrop(user, clayWad, false, true);

		}

		return resultingAmount;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAFB9D6;
	}
}
