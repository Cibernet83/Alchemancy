package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RustyProperty extends AbstractTimerProperty
{
	public static final float RUST_DURATION = 72000; //Items fully rust after an hour

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource) {
		if(damageSource.is(DamageTypes.LIGHTNING_BOLT))
			resetStartTimestamp(weapon);
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, @Nullable LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{
		//if(!level.isClientSide())
		setData(stack, getData(stack) - 100L);
		return random.nextFloat() <= getRustAmount(stack) * 0.75f ? resultingAmount * 2 : resultingAmount;
	}

	protected void setStartTimestamp(ItemStack stack)
	{
		long dayTime = CommonUtils.getLevelData().getDayTime();
		long timestamp = getData(stack);
		if(dayTime < timestamp || timestamp == 0)
			setData(stack, CommonUtils.getLevelData().getDayTime());
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if(!user.level().isClientSide())
			setStartTimestamp(stack);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		if(!itemEntity.level().isClientSide())
			setStartTimestamp(stack);
	}

	public float getMiningSpeedMultiplier(ItemStack stack)
	{
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.getHolder(this)) ? getRustAmount(stack) + 1f : 1;
	}

	public float getRustAmount(ItemStack stack)
	{
		return Mth.clamp(getElapsedTime(stack) / RUST_DURATION, 0, 1);
	}

	@Override
	public int getColor(ItemStack stack)
	{
		return FastColor.ARGB32.lerp(getRustAmount(stack), 0xE77C56, 0x59B292);
	}
}
