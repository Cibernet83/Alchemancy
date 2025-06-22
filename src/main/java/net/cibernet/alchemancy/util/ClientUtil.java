package net.cibernet.alchemancy.util;

import net.cibernet.alchemancy.client.screen.ChromaTintingScreen;
import net.cibernet.alchemancy.client.screen.InfusionCodexIndexScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

	public static void openCodexScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(stack));
	}
	public static void openCodexScreen(Component stack) {
		Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(stack));
	}

	public static Player getLocalPlayer()
	{
		return Minecraft.getInstance().player;
	}

	public static void openChromachineScreen(ItemStack stack) {
		Minecraft.getInstance().setScreen(new ChromaTintingScreen(stack));
	}
}
