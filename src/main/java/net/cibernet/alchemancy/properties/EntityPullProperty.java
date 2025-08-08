package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.mixin.accessors.ProjectileAccessor;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EntityPullProperty<E extends Entity> extends Property
{
	final int color;
	final Class<E> targetEntities;
	final float radius;
	final boolean onUse;
	final float pullStrength;

	public EntityPullProperty(int color, Class<E> targetEntities, float radius, boolean onUse, float pullStrength) {
		this.color = color;
		this.targetEntities = targetEntities;
		this.radius = radius;
		this.onUse = onUse;
		this.pullStrength = pullStrength;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(onUse)
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return onUse ? 72000 : super.modifyUseDuration(stack, original, result);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!onUse || user.getUseItem() == stack)
			vacuum(user);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		vacuum(itemEntity);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
		vacuum(null, root.getLevel(), root.getBlockPos().getCenter());
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		vacuum(projectile);
	}

	private void vacuum(Entity user)
	{
		vacuum(user, user.level(), user.getEyePosition());
	}

	private void vacuum(@Nullable Entity source, Level level, Vec3 center)
	{
		for(E target : level.getEntitiesOfClass(targetEntities, CommonUtils.boundingBoxAroundPoint(center, radius)))
		{
			if(target.equals(source))
				continue;

			double distanceTo = target.position().distanceTo(center);

			if(source != null && target instanceof Projectile projectile)
			{
				if(source.equals(projectile.getOwner()))
					continue;
				else if(distanceTo < 0.5)
				{
					((ProjectileAccessor)projectile).invokeOnHit(new EntityHitResult(source));
					continue;
				}
			}

			float strength = (float) Math.max(0, radius - distanceTo) * .05f * this.pullStrength;

			target.hasImpulse = true;
			Vec3 vec3 = target.getDeltaMovement();
			Vec3 vec31 = target.position().subtract(center).normalize().scale(strength);

			target.setDeltaMovement(vec3.scale(1 - 0.5 * (1 - distanceTo / radius)).subtract(vec31));
		}
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return onUse ? Optional.of(UseAnim.BOW) : current;
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}
}
