package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.mixin.accessors.EntityAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.ArrowNockEvent;

public class PhasingProperty extends Property
{
	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult.getType() == HitResult.Type.BLOCK)
			event.setCanceled(true);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(projectile instanceof AbstractArrow arrow)
		{
			arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
		}
		else projectile.noPhysics = true;
	}

	@Override
	public TriState allowArrowClipBlocks(AbstractArrow arrow, ItemStack stack) {
		return TriState.TRUE;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x6132AD;
	}
}
