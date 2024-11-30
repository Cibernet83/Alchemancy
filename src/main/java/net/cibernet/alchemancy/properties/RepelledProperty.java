package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class RepelledProperty<E extends Entity> extends Property
{
	final int color;
	final Class<E> targetEntities;
	final float radius;
	final boolean onUse;

	public RepelledProperty(int color, Class<E> targetEntities, float radius, boolean onUse) {
		this.color = color;
		this.targetEntities = targetEntities;
		this.radius = radius;
		this.onUse = onUse;
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
			repelUser(user);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		repelUser(itemEntity);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		repelUser(projectile);
	}

	private void repelUser(Entity user)
	{
		for(Entity target : user.level().getEntitiesOfClass(targetEntities, CommonUtils.boundingBoxAroundPoint(user.position(), radius)))
		{
			if(target.equals(user))
				continue;

			double distanceTo = target.position().distanceTo(user.position());

			float strength = (float) Math.max(0, radius - distanceTo) * .05f;

			user.hasImpulse = true;
			Vec3 vec3 = user.getDeltaMovement();
			Vec3 vec31 = target.position().subtract(user.position()).normalize().scale(strength);

			user.setDeltaMovement(vec3.subtract(vec31));
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}
}
