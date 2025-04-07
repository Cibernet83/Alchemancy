package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MalleableProperty extends Property
{
	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		if(stack.isDamageableItem())
			stack.setDamageValue(stack.getMaxDamage() - 1);

		itemEntity.level().addFreshEntity(new ItemEntity(itemEntity.level(), itemEntity.position().x, itemEntity.position().y, itemEntity.position().z, getUnshapedClay(stack)));
		stack.setCount(0);
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{

		if(user != null && stack.getMaxDamage() <= stack.getDamageValue() + resultingAmount)
		{
			stack.setDamageValue(stack.getMaxDamage() - 1);

			ItemStack clay = getUnshapedClay(stack);

			if(user.getMainHandItem() == stack)
				user.setItemInHand(InteractionHand.MAIN_HAND, clay);
			else if(user.getOffhandItem() == stack)
				user.setItemInHand(InteractionHand.OFF_HAND, clay);
			else if(user instanceof Player player)
			{
				if(!player.addItem(clay))
					player.drop(clay, true);
			}
			else HollowProperty.nonPlayerDrop(user, clay, false, true);

		}

		return resultingAmount;
	}

	public ItemStack getUnshapedClay(ItemStack stackToStore)
	{
		ItemStack clay = AlchemancyItems.UNSHAPED_CLAY.toStack();
		AlchemancyProperties.CLAY_MOLD.get().setData(clay, stackToStore.copy());

		InfusedPropertiesHelper.forEachProperty(stackToStore, propertyHolder ->
		{
			if(propertyHolder.is(AlchemancyTags.Properties.RETAINED_BY_UNSHAPED_CLAY))
			{
				InfusedPropertiesHelper.addProperty(clay, propertyHolder);
				if(propertyHolder.value() instanceof IDataHolder<?> dataHolder)
					dataHolder.copyData(stackToStore, clay);
			}
		});

		return clay;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAFB9D6;
	}
}
