package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class WaterWalkingProperty extends Property {

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (slot != EquipmentSlot.FEET) return;

		if (user.level().getFluidState(user.getBlockPosBelowThatAffectsMyMovement()).is(Fluids.WATER)) {
			user.setOnGround(true);
			var vec = user.getDeltaMovement();

			var hit = user.level().clip(new ClipContext(user.position(), user.getBlockPosBelowThatAffectsMyMovement().getBottomCenter(), ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, user));
			user.setDeltaMovement(new Vec3(vec.x, Math.max(hit.getLocation().y() - user.getY(), vec.y), vec.z));
			user.resetFallDistance();

			for(int i = 0; i < 2; i++)
				user.level().addParticle(SparklingProperty.getParticles(stack).orElse(ParticleTypes.SPLASH), user.getRandomX(1), user.getY(), user.getRandomZ(1), 0, 0, 0);
		}

	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x47CAFF;
	}
}
