package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExperiencedProperty extends Property
{

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		if(resultingAmount > 0 && user.getRandom().nextFloat() < 0.3f)
			createXp(stack, user.level(), user.position(), Math.max(1, user.getRandom().nextInt(resultingAmount)));
		return super.modifyDurabilityConsumed(stack, user, originalAmount, resultingAmount);
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		int damage = stack.getMaxDamage() - stack.getDamageValue();
		int amount = stack.isDamageableItem() ?
				(int) (damage * (itemEntity.getRandom().nextFloat() * 0.25f + 0.75f)) :
				(3 + itemEntity.getRandom().nextInt(5) + itemEntity.getRandom().nextInt(5)) * stack.getCount();

		createXp(stack, itemEntity.level(), itemEntity.position(), amount);
	}

	public void createXp(ItemStack stack, Level level, Vec3 position, int amount)
	{
		if (level instanceof ServerLevel) {
			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.WISE))
				amount = (int) (amount * 1.5f);
			ExperienceOrb.award((ServerLevel)level, position, amount);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xCAFF59;
	}

	@Override
	public int getPriority() {
		return Priority.LOWEST;
	}
}
