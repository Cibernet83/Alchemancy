package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber
public class BouncyProperty extends Property
{
	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {

		Vec3 sourcePos = source == target ? target.getEyePosition().add(target.getLookAngle().scale(10)) :
				source != null ? source.position() :
				damageSource.getSourcePosition() != null ? damageSource.getSourcePosition() :
				damageSource.getDirectEntity() != null ? damageSource.getDirectEntity().position() : null;

		if(sourcePos != null)
			knockBack(target, sourcePos);
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		onActivation(event.getEntity(), event.getEntity(), event.getItemStack());
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		if(slot.isArmor() || user.getUseItem() == weapon)
		{

			Vec3 attackPos = damageSource.getSourcePosition();
			if(attackPos == null && damageSource.getDirectEntity() != null)
				attackPos = damageSource.getDirectEntity().position();

			if(attackPos != null)
				knockBack(user, attackPos);
		}
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

		Vec3 attackPos = damageSource.getSourcePosition();
		if(attackPos == null && damageSource.getDirectEntity() != null)
			attackPos = damageSource.getDirectEntity().position();
		if(attackPos == null && user != null)
			attackPos = user.position();
		if(attackPos != null)
			knockBack(target, attackPos);
	}

	public static void knockBack(Entity target, Vec3 sourcePos)
	{
		target.hasImpulse = true;
		Vec3 vec3 = target.getDeltaMovement();
		float strength = target.onGround() ? 2 : 1;
		Vec3 vec31 = sourcePos.subtract(target.position()).normalize().scale(strength);
		target.setDeltaMovement(vec3.x * 0.5 - vec31.x, vec3.y * 0.5 - vec31.y, vec3.z * 0.5 - vec31.z);
	}

	private static final HashMap<UUID, Vec3> BOUNCE_TARGETS = new HashMap<>();

	@Override
	public void onFall(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingFallEvent event)
	{
		if(slot == EquipmentSlot.FEET && event.getDistance() >= 0.2f)
		{
			event.setDamageMultiplier(0);
			if(user.level().isClientSide())
				BOUNCE_TARGETS.put(user.getUUID(), user.getDeltaMovement());
		}
	}

	@SubscribeEvent
	private static void onEntityTickPost(EntityTickEvent.Post event)
	{
		Entity user = event.getEntity();
		UUID uuid = user.getUUID();
		if( BOUNCE_TARGETS.containsKey(uuid))
		{
			user.hurtMarked = true;
			user.setDeltaMovement(BOUNCE_TARGETS.get(uuid).multiply(1, -0.8, 1));
			BOUNCE_TARGETS.remove(uuid);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x9EEF92;
	}
}
