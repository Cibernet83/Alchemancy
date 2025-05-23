package net.cibernet.alchemancy.client.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticleBase;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class SparkParticle extends DustParticleBase<SparkParticle.Options> {

	protected SparkParticle(
			ClientLevel level,
			double x,
			double y,
			double z,
			double xSpeed,
			double ySpeed,
			double zSpeed,
			SparkParticle.Options options,
			SpriteSet sprites
	) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, options, sprites);
		float f = this.random.nextFloat() * 0.4F + 0.6F;
		this.rCol = this.randomizeColor(options.getColor().x(), f);
		this.gCol = this.randomizeColor(options.getColor().y(), f);
		this.bCol = this.randomizeColor(options.getColor().z(), f);
	}

	public static class Options extends ScalableParticleOptionsBase {

		public static final MapCodec<SparkParticle.Options> CODEC = RecordCodecBuilder.mapCodec(
				p_341566_ -> p_341566_.group(
								ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(p_253371_ -> p_253371_.color),
								SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)
						)
						.apply(p_341566_, SparkParticle.Options::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, SparkParticle.Options> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VECTOR3F, p_319429_ -> p_319429_.color, ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale, SparkParticle.Options::new
		);
		private final Vector3f color;

		public Options(Vector3f color, float scale) {
			super(scale);
			this.color = color;
		}

		@Override
		public ParticleType<SparkParticle.Options> getType() {
			return AlchemancyParticles.SPARK.get();
		}

		public Vector3f getColor() {
			return this.color;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SparkParticle.Options> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		public Particle createParticle(
				SparkParticle.Options type,
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