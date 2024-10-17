package net.cibernet.alchemancy.util;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ITintModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.concurrent.atomic.AtomicInteger;

public class CommonUtils
{

	public static AABB boundingBoxAroundPoint(Vec3 center, float radius)
	{
		return new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius, center.y + radius, center.z + radius);
	}

	public static RegistryAccess registryAccessStatic() {
		final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		return currentServer != null ? currentServer.registryAccess() : ClientUtil.registryAccess();

	}

	public static LevelData getLevelData()
	{
		final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		return currentServer != null ? currentServer.overworld().getLevelData() : ClientUtil.getCurrentLevel().getLevelData();
	}

	public static void modifyTint(ItemStack itemStack, int tintIndex, LocalIntRef localTint)
	{
		localTint.set(getPropertyDrivenTint(itemStack, tintIndex, localTint.get()));
	}

	public static int getPropertyDrivenTint(ItemStack itemStack, int tintIndex, int originalTint)
	{
		AtomicInteger localTint = new AtomicInteger(originalTint);
		InfusedPropertiesHelper.forEachProperty(itemStack, propertyHolder -> {
			if(propertyHolder.value() instanceof ITintModifier property)
				localTint.set(property.getTint(itemStack, tintIndex, originalTint, localTint.get()));
		});
		return localTint.get();
	}

	public static int getPropertyDrivenTint(ItemStack itemStack)
	{
		return getPropertyDrivenTint(itemStack, 0, 0xFFFFFFFF);
	}


	private static void legacyElasticRangeLeashBehaviour(Entity entity, Entity leashHolder, float distance) {
		double d0 = (leashHolder.getX() - entity.getX()) / (double)distance;
		double d1 = (leashHolder.getY() - entity.getY()) / (double)distance;
		double d2 = (leashHolder.getZ() - entity.getZ()) / (double)distance;
		entity.setDeltaMovement(
				entity.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2))
		);
	}
}
