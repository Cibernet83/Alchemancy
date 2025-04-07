package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

public class HardenedProperty extends Property
{
	@Override
	public int modifyDurabilityConsumed(ItemStack stack, ServerLevel level, LivingEntity user, int originalAmount, int resultingAmount, RandomSource random)
	{
		if(resultingAmount == 1)
			return random.nextFloat() < 0.1f ? 0 : resultingAmount;
		else return resultingAmount / 2;
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult.getType() == HitResult.Type.BLOCK && rayTraceResult instanceof BlockHitResult blockHitResult &&
				projectile.level().getBlockState(blockHitResult.getBlockPos()).is(AlchemancyTags.Blocks.BROKEN_BY_HARDENED))
			projectile.level().destroyBlock(blockHitResult.getBlockPos(), true, projectile);
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		if(dataType == DataComponents.FOOD && data instanceof FoodProperties foodProperties)
			return new FoodProperties(foodProperties.nutrition(), foodProperties.saturation(), foodProperties.canAlwaysEat(), foodProperties.eatSeconds() * 2, foodProperties.usingConvertsTo(), foodProperties.effects());
		else return super.modifyDataComponent(stack, dataType, data);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xB75A40;
	}
}
