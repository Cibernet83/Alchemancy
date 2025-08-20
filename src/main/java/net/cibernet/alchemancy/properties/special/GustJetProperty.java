package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Optional;

public class GustJetProperty extends Property {

	private static final float PARTICLE_SCALE = 1.2f;
	public static final ParticleOptions PARTICLES = new SparkParticleOptions(AlchemancyParticles.GUST_DUST.get(), Vec3.fromRGB24(0xE0E6FF).toVector3f(), PARTICLE_SCALE, false);
	//private static final float DISTANCE = 6;

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {
		Level level = user.level();
		Vec3 eyePos = user.getEyePosition();
		float maxDistance = getMaxDistance(stack);

		var distance = level.clip(new ClipContext(eyePos, eyePos
						.add(user.getLookAngle().scale(maxDistance)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, user)).getLocation()
				.distanceTo(eyePos);
		var pow = Mth.lerp(Mth.clamp(distance / maxDistance, 0, 1), 0.35f, 0.085f);

		playEffects(level, user, stack, eyePos.add(user.getLookAngle()), user.getLookAngle(), (1 - distance / maxDistance) * 1.5f + 0.75f, (float) (distance / maxDistance), 0.1f, 0.1f);
		Vec3 movementVector = user.getLookAngle().scale(pow);

		pushEntities(level, user, maxDistance, eyePos, user.getLookAngle(), stack);

		if ((user.isShiftKeyDown() && user.onGround()) || shouldPull(stack)) return;

		user.setDeltaMovement(user.getDeltaMovement().subtract(movementVector));
		user.hasImpulse = true;

		if (movementVector.y() > 0.005f)
			user.fallDistance = Math.max(0, user.fallDistance - 10);

		EquipmentSlot slot = user.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		if (event.getDuration() % 40 == 5) {
			if (stack.isDamageableItem())
				stack.hurtAndBreak(1, user, slot);
			else consumeItem(user, stack, slot);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (user.isPassenger() || (slot != EquipmentSlot.FEET && slot != EquipmentSlot.BODY)) return;

		float maxDistance = getMaxDistance(stack);

		if (((LivingEntityAccessor) user).isJumping()) {

			Level level = user.level();
			Vec3 pos = user.position();
			Vec3 down = new Vec3(0, -1, 0);

			if (!level.isClientSide()) {
				if (user.tickCount % 20 == 0) {
					if (stack.isDamageableItem())
						stack.hurtAndBreak(2, user, slot);
					else consumeItem(user, stack, slot);
				}
			}

			var distance = level.clip(new ClipContext(pos, pos
							.add(down.scale(maxDistance)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, user)).getLocation()
					.distanceTo(pos);
			var pow = Mth.lerp(Mth.clamp(distance / maxDistance, 0, 1), 0.25f, 0.065f);
			Vec3 movementVector = down.scale(pow);

			playEffects(level, user, stack, pos, down, 1 - (distance / maxDistance), (float) (distance / maxDistance), user.getBbWidth(), 0);

			pushEntities(level, user, maxDistance, pos, down, stack);

			user.setDeltaMovement(user.getDeltaMovement().subtract(movementVector));
			user.hasImpulse = true;

			if (movementVector.y() > 0.005f)
				user.fallDistance = Math.max(0, user.fallDistance - 10);

			if (user.tickCount % 40 == 0) {
				if (stack.isDamageableItem())
					stack.hurtAndBreak(1, user, slot);
				else consumeItem(user, stack, slot);
			}
		}
	}

	private void pushEntities(Level level, LivingEntity user, float pushDistance, Vec3 startPos, Vec3 angle, ItemStack stack) {

		var entities = level.getEntities(user, CommonUtils.boundingBoxAroundPoint(startPos, pushDistance), entity ->
				entity.position().distanceToSqr(startPos) <= pushDistance * pushDistance &&
						entity.position().vectorTo(user.position()).normalize().dot(angle) < -0.75);

		boolean pull = shouldPull(stack);

		for (Entity entity : entities) {

			var vec = pull ?
					entity.position().subtract(startPos).normalize().scale(Mth.lerp(Mth.clamp(entity.position().distanceTo(startPos) / pushDistance, 0, 1), -0.15f, -0.25f)) :
					entity.position().subtract(startPos).normalize().scale(Mth.lerp(Mth.clamp(entity.position().distanceTo(startPos) / pushDistance, 0, 1), 0.35f, 0.85f));

			entity.setDeltaMovement(user.getDeltaMovement().add(vec));
			entity.hasImpulse = true;
			if (vec.y() > 0.005f)
				entity.fallDistance = Math.max(0, user.fallDistance - 10);

			if (!level.isClientSide() && user.getRandom().nextFloat() < 0.2f && entity instanceof LivingEntity living)
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onAttack(user, stack, user.damageSources().generic(), living));

		}

	}

	private boolean shouldPull(ItemStack stack) {
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.GRAPPLING);
	}

	private float getMaxDistance(ItemStack stack) {
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.EXTENDED) ? 10 : 6;
	}

	private void playEffects(Level level, LivingEntity user, ItemStack stack, Vec3 effectPosition, Vec3 movementVector, double pow, float soundPitch, float hOff, float vOff) {
		RandomSource random = user.getRandom();

		if (level.isClientSide()) {

			var particleSpeed = 1;
			var sparklingParticles = SparklingProperty.getParticles(stack);
			var propertyParticles = new ArrayList<ParticleOptions>();

			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder ->
			{
				if (!propertyHolder.is(AlchemancyTags.Properties.CHANGES_GUST_JET_WIND_COLOR)) return;
				propertyParticles.add(new SparkParticleOptions(AlchemancyParticles.GUST_DUST.get(), Vec3.fromRGB24(propertyHolder.value().getColor(stack)).toVector3f(), PARTICLE_SCALE, false));
			});

			for (int i = 0; i < random.nextInt(3) + 1; i++) {

				ParticleOptions particles = sparklingParticles.orElse(
						propertyParticles.isEmpty() ? PARTICLES :
								propertyParticles.get(user.getRandom().nextInt(propertyParticles.size()))
				);

				level.addParticle(particles,
						effectPosition.x() + ((random.nextFloat() - 0.5f) * hOff),
						effectPosition.y() + ((random.nextFloat() - 0.5f) * vOff),
						effectPosition.z() + ((random.nextFloat() - 0.5f) * hOff),
						(movementVector.x() * particleSpeed + user.getDeltaMovement().x() + ((random.nextFloat() - 0.5f) * pow)),
						(movementVector.y() * particleSpeed + user.getDeltaMovement().y() + ((random.nextFloat() - 0.5f) * pow)),
						(movementVector.z() * particleSpeed + user.getDeltaMovement().z() + ((random.nextFloat() - 0.5f) * pow))
				);
			}
		}

		if (random.nextFloat() > 0.15f)
			level.playSound(null, user, AlchemancySoundEvents.GUST_JET.value(), SoundSource.BLOCKS, 0.25f, soundPitch);

	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);

	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return current.isEmpty() && original == UseAnim.NONE ? Optional.of(UseAnim.BOW) : current;
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE0E6FF;
	}
}
