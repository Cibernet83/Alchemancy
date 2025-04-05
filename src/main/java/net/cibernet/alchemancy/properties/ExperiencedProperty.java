package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ExperiencedProperty extends Property
{

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{
		if(user != null && resultingAmount > 0 && random.nextFloat() < 0.3f)
			createXp(stack, level, user.position(), Math.max(1, user.getRandom().nextInt(resultingAmount)));
		return resultingAmount;
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
