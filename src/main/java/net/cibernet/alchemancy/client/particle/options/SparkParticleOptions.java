package net.cibernet.alchemancy.client.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class SparkParticleOptions extends ScalableParticleOptionsBase {

	public static MapCodec<SparkParticleOptions> codec(ParticleType<SparkParticleOptions> particleType) {
		return RecordCodecBuilder.mapCodec(
				p_341566_ -> p_341566_.group(
								ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(p_253371_ -> p_253371_.color),
								SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale),
								Codec.BOOL.optionalFieldOf("stationary", true).forGetter(SparkParticleOptions::stationary)
						)
						.apply(p_341566_, (color, scale, stationary) -> new SparkParticleOptions(particleType, color, scale, stationary))
		);
	}

	private boolean stationary() {
		return stationary;
	}

	public static StreamCodec<RegistryFriendlyByteBuf, SparkParticleOptions> streamCodec(ParticleType<SparkParticleOptions> particleType) {
		return StreamCodec.composite(
				ByteBufCodecs.VECTOR3F, p_319429_ -> p_319429_.color,
				ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale,
				ByteBufCodecs.BOOL, SparkParticleOptions::stationary,
				(color, scale, stationary) -> new SparkParticleOptions(particleType, color, scale, stationary)
		);
	}

	public final ParticleType<SparkParticleOptions> type;
	public final Vector3f color;
	public final boolean stationary;

	public SparkParticleOptions(ParticleType<SparkParticleOptions> type, Vector3f color, float scale, boolean stationary) {
		super(scale);
		this.color = color;
		this.type = type;
		this.stationary = stationary;
	}

	public SparkParticleOptions(Vector3f color, float scale) {
		this(AlchemancyParticles.SPARK.get(), color, scale, true);
	}

	@Override
	public ParticleType<SparkParticleOptions> getType() {
		return type;
	}

	public Vector3f getColor() {
		return this.color;
	}
}
