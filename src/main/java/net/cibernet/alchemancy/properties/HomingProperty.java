package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyProperties;
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

import java.util.List;
import java.util.function.Predicate;

public class HomingProperty<E extends Entity> extends Property
{
	final int color;
	final Class<E> targetEntities;
	final float radius;
	final float strength;
	final EffectType type;
	final Predicate<E> entityPredicate;

	public HomingProperty(int color, Class<E> targetEntities, float radius, float strength, EffectType type, Predicate<E> entityPredicate) {
		this.color = color;
		this.targetEntities = targetEntities;
		this.radius = radius;
		this.type = type;
		this.entityPredicate = entityPredicate;
		this.strength = strength;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(type == EffectType.ON_USE)
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return type == EffectType.ON_USE ? 72000 : super.modifyUseDuration(stack, original, result);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(type == EffectType.ALWAYS || (type == EffectType.ON_USE && user.getUseItem() == stack))
			pullUser(user);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		pullUser(itemEntity);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(!AlchemancyProperties.LOYAL.value().isReturning(projectile))
			pullUser(projectile);
	}

	private void pullUser(Entity user)
	{

		List<E> targets = user.level().getEntitiesOfClass(targetEntities, CommonUtils.boundingBoxAroundPoint(user.position(), radius));
		targets.removeIf(target -> (target.equals(user) || (user instanceof Projectile projectile && target.equals(projectile.getOwner())) || !entityPredicate.test(target)));
		targets.sort((target1, target2) -> (int)(target1.getEyePosition().distanceTo(user.position()) - target2.getEyePosition().distanceTo(user.position())));

		if(targets.isEmpty())
			return;

		E target = targets.getFirst();
		double distanceTo = target.getEyePosition().distanceTo(user.position());

		float strength = (float) Math.max(0, radius - distanceTo) * .05f * this.strength;

		user.hasImpulse = true;
		Vec3 vec3 = user.getDeltaMovement().scale(0.5);
		Vec3 vec31 = target.getEyePosition().subtract(user.position()).normalize().scale(strength);

		user.setDeltaMovement(vec3.add(vec31));
	}

	@Override
	public int getColor(ItemStack stack) {
		return color;
	}

	public enum EffectType
	{
		ALWAYS,
		ON_USE,
		PROJECTILE_ONLY
	}
}
