package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.UUID;

public class TelekineticProperty extends Property implements IDataHolder<UUID> {


	public static final ParticleOptions PARTICLES = new DustColorTransitionOptions(Vec3.fromRGB24(0x4BEC13).toVector3f(), Vec3.fromRGB24(0x06672FF).toVector3f(), 1);

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		Player user = event.getEntity();
		double distance = user.blockInteractionRange() * 3;

		if (user.level().isClientSide) return;

		var hit = ProjectileUtil.getEntityHitResult(user, user.getEyePosition(), user.getEyePosition().add(user.getLookAngle().scale(distance)),
				CommonUtils.boundingBoxAroundPoint(user.getEyePosition(), (float) distance), e -> true, distance);
		if (hit == null) return;

		setData(event.getItemStack(), hit.getEntity().getUUID());

		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);
	}

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {

		UUID targetUuid = getData(stack);
		if (!(user.level() instanceof ServerLevel level) || targetUuid == null) return;

		var target = level.getEntity(targetUuid);

		if (target == null) {
			user.stopUsingItem();
			return;
		}

		var eyePos = user.getEyePosition();
		double distance = user instanceof Player player ? player.blockInteractionRange() : 5;

		var newDelta = eyePos.add(user.getLookAngle().scale(distance).subtract(0, target.getBbHeight() * 0.5f, 0))
				.subtract(target.position()).scale(0.3f);

		if(target instanceof AbstractArrow arrow &&
				((AbstractArrowAccessor)arrow).accessInGround() &&
				level.noBlockCollision(arrow, arrow.getBoundingBox().move(newDelta)))
			((AbstractArrowAccessor)arrow).setInGround(false);

		target.resetFallDistance();
		target.setDeltaMovement(newDelta);
		target.hasImpulse = true;
		target.hurtMarked = true;

		level.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLES), target.getRandomX(1), target.getRandomY(), target.getRandomZ(1), 3, 0.1, 0.1, 0.1, 0);
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public void onStopUsingItem(ItemStack stack, LivingEntity user, LivingEntityUseItemEvent.Stop event) {
		removeData(stack);
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.sineColorsOverTime(5f, 0x4BEC13, 0x06672FF);
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return Optional.of(UseAnim.BOW);
	}

	@Override
	public UUID readData(CompoundTag tag) {
		return tag.hasUUID("target") ? tag.getUUID("target") : null;
	}

	@Override
	public CompoundTag writeData(UUID data) {
		return new CompoundTag() {{
			if (data != null)
				putUUID("target", data);
		}};
	}

	@Override
	public UUID getDefaultData() {
		return null;
	}
}
