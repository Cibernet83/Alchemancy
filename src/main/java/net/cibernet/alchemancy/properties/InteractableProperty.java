package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class InteractableProperty extends Property
{
	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		ItemStack stack = event.getItemStack();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(event.getEntity(), event.getTarget(), stack));
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(event.getEntity(), event.getEntity(), stack));

		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
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
