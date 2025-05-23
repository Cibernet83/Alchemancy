package net.cibernet.alchemancy.registries;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.particle.GlowingOrbParticle;
import net.cibernet.alchemancy.client.particle.SparkParticle;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyParticles
{
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOWING_ORB = REGISTRY.register("glowing_orb", () -> new SimpleParticleType(true));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WARHAMMER_FLAME = REGISTRY.register("warhammer_flame", () -> new SimpleParticleType(true));
	public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticle.Options>> SPARK = register("spark", true, p_337461_ -> SparkParticle.Options.CODEC, p_319435_ -> SparkParticle.Options.STREAM_CODEC);

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(GLOWING_ORB.get(), GlowingOrbParticle.Provider::new);
		event.registerSpriteSet(WARHAMMER_FLAME.get(), FlameParticle.Provider::new);
		event.registerSpriteSet(SPARK.get(), SparkParticle.Provider::new);
	}

	private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
			String name,
			boolean overrideLimitter,
			final Function<ParticleType<T>, MapCodec<T>> codecGetter,
			final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter
	) {
		return REGISTRY.register(name, () -> new ParticleType<T>(overrideLimitter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		});
	}
}
