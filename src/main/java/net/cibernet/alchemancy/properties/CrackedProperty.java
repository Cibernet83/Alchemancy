package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CrackedProperty extends Property
{
	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{
		return resultingAmount * (random.nextFloat() < 0.4f ? 2 : 1);
	}

	@Override
	public void onActivation(@Nullable Entity user, Entity target, ItemStack weapon, DamageSource damageSource)
	{

		if(target.level() instanceof ServerLevel serverLevel && PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
		{
			int durabilityConsumed = PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 1);
			if(user instanceof LivingEntity living)
				weapon.hurtAndBreak(durabilityConsumed, living, EquipmentSlot.MAINHAND);
			else weapon.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {});
		}
		else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x7A7A7A;
	}
}
