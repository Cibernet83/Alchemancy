package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.events.handler.MobTemptHandler;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SensitiveProperty extends Property
{
	public static <E extends Entity> boolean sense(Level level, Class<E> classSelector, AABB bounds, List<E> ignore, Consumer<E> action)
	{
		List<E> entitiesInBounds = level.getEntitiesOfClass(classSelector, bounds);
		entitiesInBounds.removeAll(ignore);

		for (E target : entitiesInBounds) {
			action.accept(target);
		}

		return !entitiesInBounds.isEmpty();
	}

	public static <E extends Entity> boolean senseAndActivateOnTargets(Level level, BlockPos pos, ItemStack stack, Class<E> classSelector, E... ignore)
	{
		return sense(level, classSelector, new AABB(pos), List.of(ignore), (entity) ->
		{
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
					propertyHolder.value().onActivationByBlock(level, pos.getCenter(), entity, stack));
		});
	}

	public static <E extends Entity> boolean senseAndActivateOnTargets(Entity source, ItemStack stack, float radius, Class<E> classSelector, E... ignore)
	{
		return sense(source.level(), classSelector, source.getBoundingBox().inflate(radius), List.of(ignore), (entity) ->
		{
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
					propertyHolder.value().onActivation(source, entity, stack));
		});
	}
	public static <E extends Entity> boolean senseAndActivateOnSelf(Entity source, ItemStack stack, float radius, Class<E> classSelector, E... ignore)
	{
		if(sense(source.level(), classSelector, source.getBoundingBox().inflate(radius), List.of(ignore), (entity) -> {}))
		{
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
					propertyHolder.value().onActivation(source, source, stack));
			return true;
		}

		return false;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(user instanceof Player player)
		{
			ItemCooldowns cooldowns = player.getCooldowns();
			if(!cooldowns.isOnCooldown(stack.getItem()) && senseAndActivateOnSelf(user, stack, 1, LivingEntity.class, user))
			{
				player.level().playSound(player, player, SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.5f, 0.6f);
				cooldowns.addCooldown(stack.getItem(), 100);
			}
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(senseAndActivateOnSelf(projectile, stack, 1, LivingEntity.class, projectile.getOwner() instanceof LivingEntity owner ? owner : null))
		{
			projectile.level().playSound(null, projectile.position().x, projectile.position().y, projectile.position().z, SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.5f, 0.6f);
			BrittleProperty.breakProjectile(stack, projectile);
		}
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> livingEntities)
	{
		if(root.getTickCount() % 20 == 0 && senseAndActivateOnTargets(root.getLevel(), root.getBlockPos(), root.getItem(), Entity.class))
				root.getLevel().playSound(null, root.getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.5f, 0.6f);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		if (root.getTickCount() % 20 == 0) {
			playRootedParticles(root, random, DustColorTransitionOptions.SCULK_TO_REDSTONE);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE29804;
	}
}
