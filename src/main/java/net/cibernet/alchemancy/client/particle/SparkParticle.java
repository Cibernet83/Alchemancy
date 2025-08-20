package net.cibernet.alchemancy.client.particle;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SparkParticle extends DustParticleBase<SparkParticleOptions> {

	protected SparkParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double xSpeed,
			double ySpeed,
			double zSpeed,
			SparkParticleOptions options,
			SpriteSet sprites
	) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
//		float f = this.random.nextFloat() * 0.4F + 0.6F;
//		this.rCol = this.randomizeColor(options.getColor().x(), f);
//		this.gCol = this.randomizeColor(options.getColor().y(), f);
//		this.bCol = this.randomizeColor(options.getColor().z(), f);

		float f = (float) Math.random() * 0.4F - 0.2F;
		this.rCol = Math.clamp(options.getColor().x() + f, 0, 1);
		this.gCol = Math.clamp(options.getColor().y() + f, 0, 1);
		this.bCol = Math.clamp(options.getColor().z() + f, 0, 1);

		if (!options.stationary)
			this.setParticleSpeed(xSpeed, ySpeed, zSpeed);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SparkParticleOptions> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(
				SparkParticleOptions type,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xSpeed,
				double ySpeed,
				double zSpeed
		) {
			return new SparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type, this.sprites);
		}
	}
}