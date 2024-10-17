package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.particle.GlowingOrbParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyParticles
{
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOWING_ORB = REGISTRY.register("glowing_orb", () -> new SimpleParticleType(true));

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(GLOWING_ORB.get(), GlowingOrbParticle.Provider::new);
	}
}
