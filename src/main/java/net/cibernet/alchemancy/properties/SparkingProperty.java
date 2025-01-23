package net.cibernet.alchemancy.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.Random;

@EventBusSubscriber(Dist.CLIENT)
public class SparkingProperty extends Property
{
	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(damageSource.is(Tags.DamageTypes.IS_PHYSICAL))
			ignite(user.level(), user.blockPosition(), Direction.DOWN);
	}

	@Override
	public void onFall(LivingEntity entity, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if((slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) && event.getDistance() > 3f)
			ignite(entity.level(), entity.blockPosition(), Direction.DOWN);
	}

	@Override
	public void onActivation(Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		ignite(source.level(), source.blockPosition(), Direction.DOWN);
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult.getType() == HitResult.Type.BLOCK && rayTraceResult instanceof BlockHitResult blockHitResult)
			ignite(projectile.level(), blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), blockHitResult.getDirection());
		else if(rayTraceResult.getType() == HitResult.Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult)
			ignite(projectile.level(), entityHitResult.getEntity().blockPosition(), Direction.UP);
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		ignite(itemEntity.level(), itemEntity.blockPosition(), Direction.DOWN);
	}

	public static void ignite(Level level, BlockPos pos, Direction direction)
	{
		if(!level.isClientSide())
		{
			if (BaseFireBlock.canBePlacedAt(level, pos, direction) || level.getBlockState(pos).canBeReplaced()) {
				level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState blockstate1 = BaseFireBlock.getState(level, pos);
				level.setBlock(pos, blockstate1, 11);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return FastColor.ARGB32.lerp(sparkColor, 0x717171, 0xFFC251);
	}

	private static final Random random = new Random();
	private static float sparkColor = 0;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	private static void onClientTick(ClientTickEvent.Pre event)
	{
		if(random.nextFloat() < 0.05f)
			sparkColor = 1;
		else sparkColor = Math.max(0, sparkColor- 1/30f);
	}
}
