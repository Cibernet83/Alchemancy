package net.cibernet.alchemancy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class WayfindingUtil
{

	private static final CompassWobble wobble = new CompassWobble();
	private static final CompassWobble wobbleRandom = new CompassWobble();

	public static float getRotationTowardsCompassTarget(Entity entity, long ticks, BlockPos pos)
	{
		double d0 = getAngleFromEntityToPos(entity, pos);
		double d1 = getWrappedVisualRotationY(entity);
		if (entity instanceof Player player && player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
			if (wobble.shouldUpdate(ticks)) {
				wobble.update(ticks, 0.5 - (d1 - 0.25));
			}

			double d3 = d0 + wobble.rotation;

			return Mth.positiveModulo((float)d3, 1.0F);
		}

		double d2 = 0.5 - (d1 - 0.25 - d0);
		return Mth.positiveModulo((float)d2, 1.0F);
	}

	public static float getRandomlySpinningRotation(int seed, long ticks) {
		if (wobbleRandom.shouldUpdate(ticks)) {
			wobbleRandom.update(ticks, Math.random());
		}

		double d0 = wobbleRandom.rotation + (double)((float)hash(seed) / 2.1474836E9F);
		return Mth.positiveModulo((float)d0, 1.0F);
	}

	public static double getAngleFromEntityToPos(Entity entity, BlockPos pos) {
		Vec3 vec3 = Vec3.atCenterOf(pos);
		return Math.atan2(vec3.z() - entity.getZ(), vec3.x() - entity.getX()) / (float) (Math.PI * 2);
	}

	public static double getWrappedVisualRotationY(Entity entity) {
		return Mth.positiveModulo((double)(entity.getVisualRotationYInDegrees() / 360.0F), 1.0);
	}

	public static boolean shouldWobbleUpdate(long ticks) {
		return wobble.shouldUpdate(ticks);
	}

	@OnlyIn(Dist.CLIENT)
	static class CompassWobble {
		double rotation;
		double previousRotation;
		private double deltaRotation;
		private long lastUpdateTick;

		boolean shouldUpdate(long ticks) {
			return this.lastUpdateTick != ticks;
		}

		void update(long ticks, double rotation) {
			this.lastUpdateTick = ticks;
			double d0 = rotation - this.rotation;
			d0 = Mth.positiveModulo(d0 + 0.5, 1.0) - 0.5;
			this.deltaRotation += d0 * 0.1;
			this.deltaRotation *= 0.8;

			this.previousRotation = this.rotation;
			this.rotation = Mth.positiveModulo(this.rotation + this.deltaRotation, 1.0);
		}
	}

	private static int hash(int value) {
		return value * 1327217883;
	}
}
