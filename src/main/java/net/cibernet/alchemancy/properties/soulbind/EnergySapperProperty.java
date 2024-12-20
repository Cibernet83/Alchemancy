package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnergySapperProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!user.level().isClientSide() && user.getRandom().nextFloat() < 0.01f && stack.getDamageValue() > 10 && user instanceof Player player && player.getFoodData().getFoodLevel() > 0)
		{
			player.causeFoodExhaustion(10f);
			stack.setDamageValue(stack.getDamageValue() - 10);
		}
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(target instanceof Player player)
			player.causeFoodExhaustion(20f);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD14A59;
	}
}
