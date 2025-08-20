package net.cibernet.alchemancy.registries;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.particle.SparkParticle;
import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WARHAMMER_FLAME = REGISTRY.register("warhammer_flame", () -> new SimpleParticleType(true));
	public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> SPARK = register("spark", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec);
	public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> GUST_DUST = register("gust_dust", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec);
	public static final DeferredHolder<ParticleType<?>, ParticleType<SparkParticleOptions>> CLOUD_SMOKE = register("cloud_smoke", true, SparkParticleOptions::codec, SparkParticleOptions::streamCodec);

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(WARHAMMER_FLAME.get(), FlameParticle.Provider::new);
		event.registerSpriteSet(SPARK.get(), SparkParticle.Provider::new);
		event.registerSpriteSet(GUST_DUST.get(), SparkParticle.Provider::new);
		event.registerSpriteSet(CLOUD_SMOKE.get(), SparkParticle.Provider::new);
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
