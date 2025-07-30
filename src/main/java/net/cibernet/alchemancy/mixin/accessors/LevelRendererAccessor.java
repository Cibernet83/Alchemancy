package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {

	@Invoker("addParticleInternal")
	Particle invokeAddParticleInternal(ParticleOptions options,
	                                   boolean force,
	                                   double x,
	                                   double y,
	                                   double z,
	                                   double xSpeed,
	                                   double ySpeed,
	                                   double zSpeed);
}
