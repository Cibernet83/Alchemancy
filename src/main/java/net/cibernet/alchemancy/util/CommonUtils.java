package net.cibernet.alchemancy.util;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.ITintModifier;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonUtils {

	public static AABB boundingBoxAroundPoint(Vec3 center, float radius) {
		return new AABB(center.x - radius, center.y - radius, center.z - radius, center.x + radius, center.y + radius, center.z + radius);
	}

	public static RegistryAccess registryAccessStatic() {
		final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		return currentServer != null ? currentServer.registryAccess() : ClientUtil.registryAccess();

	}

	public static LevelData getLevelData() {
		final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		return currentServer != null ? currentServer.overworld().getLevelData() : ClientUtil.getCurrentLevel().getLevelData();
	}

	public static Optional<Player> getPlayerByUUID(UUID uuid) {
		final MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
		return Optional.ofNullable(currentServer != null ? currentServer.overworld().getPlayerByUUID(uuid) : ClientUtil.getCurrentLevel().getPlayerByUUID(uuid));
	}

	public static void modifyTint(ItemStack itemStack, int tintIndex, LocalIntRef localTint) {
		localTint.set(getPropertyDrivenTint(itemStack, tintIndex, localTint.get()));
	}

	public static int getPropertyDrivenTint(ItemStack itemStack, int tintIndex, int originalTint) {
		AtomicInteger localTint = new AtomicInteger(originalTint);
		InfusedPropertiesHelper.forEachProperty(itemStack, propertyHolder -> {
			if (propertyHolder.value() instanceof ITintModifier property)
				localTint.set(property.getTint(itemStack, tintIndex, originalTint, localTint.get()));
		});
		return localTint.get();
	}

	public static boolean hasPropertyDrivenAlpha(ItemStack stack) {
		AtomicBoolean translucent = new AtomicBoolean(false);
		InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> {
			if (propertyHolder.value() instanceof ITintModifier tintModifier && tintModifier.modifiesAlpha())
				translucent.set(true);
		});

		return translucent.get();
	}

	public static boolean hasPropertyDrivenTint(ItemStack stack) {
		return InfusedPropertiesHelper.getInfusedProperties(stack).stream().anyMatch(propertyHolder -> propertyHolder.value() instanceof ITintModifier);
	}

	public static int getPropertyDrivenTint(ItemStack itemStack) {
		return getPropertyDrivenTint(itemStack, 0, 0xFFFFFFFF);
	}

	public static void tickInventoryItemProperties(Player player) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = player.getItemBySlot(slot);
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEquippedTick(player, slot, stack));
		}

		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			final int currentSlot = slot;
			if (!stack.isEmpty())
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onInventoryTick(player, stack, player.level(), currentSlot, inventory.selected == currentSlot));
		}
	}

	private static void legacyElasticRangeLeashBehaviour(Entity entity, Entity leashHolder, float distance) {
		double d0 = (leashHolder.getX() - entity.getX()) / (double) distance;
		double d1 = (leashHolder.getY() - entity.getY()) / (double) distance;
		double d2 = (leashHolder.getZ() - entity.getZ()) / (double) distance;
		entity.setDeltaMovement(
				entity.getDeltaMovement().add(Math.copySign(d0 * d0 * 0.4, d0), Math.copySign(d1 * d1 * 0.4, d1), Math.copySign(d2 * d2 * 0.4, d2))
		);
	}

	public static double lerpAngle(double amount, double start, double end) {
		return (((((end - start) % 1.0) + 1.5) % 1.0) - 0.5) * amount + start;
	}

	public static float lerpAngle(float amount, float start, float end) {
		return (((((end - start) % 1.0f) + 1.5f) % 1.0f) - 0.5f) * amount + start;
	}

	public static HitResult calculateHitResult(LivingEntity user) {
		return ProjectileUtil.getHitResultOnViewVector(
				user, p_281111_ -> !p_281111_.isSpectator() && p_281111_.isPickable(), user.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)
		);
	}

	public static void applyChromaTint(ItemStack affectedItem, int color) {

//		if((color & 0xFFFFFF) == 0xFFFFFF)
//		{
//			InfusedPropertiesHelper.removeProperty(affectedItem, AlchemancyProperties.TINTED);
//			return;
//		}
		if (!InfusedPropertiesHelper.hasProperty(affectedItem, AlchemancyProperties.TINTED))
			InfusedPropertiesHelper.addProperty(affectedItem, AlchemancyProperties.TINTED);
		AlchemancyProperties.TINTED.value().setData(affectedItem, new Integer[]{color});

	}
}
