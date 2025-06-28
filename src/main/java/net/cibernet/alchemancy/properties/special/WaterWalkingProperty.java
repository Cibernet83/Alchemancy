package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.beans.EventHandler;

public class WaterWalkingProperty extends Property {

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (!(slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) || user.isShiftKeyDown() || user.isSwimming())
			return;

		if (user.level().getFluidState(user.getBlockPosBelowThatAffectsMyMovement()).is(Fluids.WATER)) {

			if(!user.onGround())
				CommonHooks.onLivingFall(user, user.fallDistance, 0);

			user.setOnGround(true);
			var vec = user.getDeltaMovement();

			var hit = user.level().clip(new ClipContext(user.position(), user.getBlockPosBelowThatAffectsMyMovement().getBottomCenter(), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, user));
			user.setDeltaMovement(new Vec3(vec.x, Math.max(hit.getLocation().y() - user.getY(), vec.y), vec.z));
			user.resetFallDistance();

			playParticles(user, user.getY(), stack, 2);
		}

	}

	public static void playParticles(Entity user, double y, ItemStack stack, int amount) {
		if (user.level() instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(ParticleTypes.BUBBLE), user.getX(), y, user.getZ(), amount, user.getBbWidth() * 0.4f, 0, user.getBbWidth() * 0.4f, 0);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x47CAFF;
	}
}
