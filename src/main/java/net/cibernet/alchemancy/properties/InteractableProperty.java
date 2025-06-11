package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InteractableProperty extends Property
{

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled())
		{
			ItemStack stack = event.getItemStack();
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(event.getEntity(), event.getEntity(), stack));

			if(event.getEntity() instanceof Player player)
				applyCooldown(player, event.getItemStack());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		if(!event.isCanceled())
		{
			ItemStack stack = event.getItemStack();
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(event.getEntity(), event.getTarget(), stack));

			if(event.getEntity() instanceof Player player)
				applyCooldown(player, event.getItemStack());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	protected static void applyCooldown(Player player, ItemStack stack) {
		var cooldowns = player.getCooldowns();
		if(!cooldowns.isOnCooldown(stack.getItem()))
			cooldowns.addCooldown(stack.getItem(), 20);
	}

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult)
	{
		ServerLevel serverlevel = blockSource.level();
		BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
		List<Entity> list = serverlevel.getEntitiesOfClass(Entity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);

		if(!list.isEmpty())
		{
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivationByBlock(serverlevel, blockpos, list.getFirst(), stack));
			InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
			return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
		}
		return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
	}

	@Override
	public @Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult)
	{
		ItemStack stack = root.getItem();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivationByBlock(user.level(), root.getBlockPos(), user, stack));

		return super.onRootedRightClick(root, user, hand, hitResult);
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult instanceof EntityHitResult entityHitResult)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(entityHitResult.getEntity(), entityHitResult.getEntity(), stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF6366;
	}
}
