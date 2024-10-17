package net.cibernet.alchemancy.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ClientUtil
{
	@OnlyIn(Dist.CLIENT)
	public static RegistryAccess registryAccess()
	{
		return Minecraft.getInstance().level.registryAccess();
	}


	public static Level getCurrentLevel() {
		return Minecraft.getInstance().level;
	}

	public static void createTrackedParticles(Entity target, ParticleOptions particle)
	{
		Minecraft.getInstance().particleEngine.createTrackingEmitter(target, particle);
	}
}
