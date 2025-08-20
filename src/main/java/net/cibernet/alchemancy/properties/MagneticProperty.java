package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.client.particle.options.SparkParticleOptions;
import net.cibernet.alchemancy.events.handler.PropertyEventHandler;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.AbstractArrowAccessor;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class MagneticProperty extends Property {

	private static final float RADIUS = 20;
	private static final float ARMOR_RANGE = 0.25f;


	public static final ParticleOptions PARTICLE_A = new SparkParticleOptions(Vec3.fromRGB24(0xFF8484).toVector3f(), 0.65f);
	public static final ParticleOptions PARTICLE_B = new SparkParticleOptions(Vec3.fromRGB24(0x8484FF).toVector3f(), 0.65f);

	@Override
	public void modifyKnockBackReceived(LivingEntity user, ItemStack stack, EquipmentSlot slot, LivingKnockBackEvent event) {

		if (slot == EquipmentSlot.FEET && user.level().getBlockState(user.getBlockPosBelowThatAffectsMyMovement()).is(AlchemancyTags.Blocks.MAGNETIC_STICKS_TO))
			event.setCanceled(true);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		Vec3 delta = user.getDeltaMovement();
		Level level = user.level();

		if (slot == EquipmentSlot.HEAD) {
			Vec3 hitStart = new Vec3(user.getX(), user.getY(1), user.getZ());
			var hit = user.level().clip(new ClipContext(hitStart, hitStart.add(0, ARMOR_RANGE, 0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, user));

			if (level.getBlockState(hit.getBlockPos()).is(AlchemancyTags.Blocks.MAGNETIC_STICKS_TO)) {
				user.setDeltaMovement(delta.x, Math.max(delta.y, 0), delta.z);
				user.resetFallDistance();
				user.setOnGround(true);
				playParticles(user, user.getY(1f), stack);
			}
		} else if (slot == EquipmentSlot.FEET) {
			if (user.level().getBlockState(user.getBlockPosBelowThatAffectsMyMovement()).is(AlchemancyTags.Blocks.MAGNETIC_STICKS_TO)) {
				playParticles(user, user.getY(0.1f), stack);
				user.setDeltaMovement(delta.x, Math.min(delta.y, 0.2f), delta.z);
			}
		} else if (slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS) {
			double y = user.getY(0.5f + (slot == EquipmentSlot.CHEST ? 0.2 : -0.2));
			double hitOffset = user.getBbWidth() * 0.5 + ARMOR_RANGE;
			Vec3 hitStart = new Vec3(user.getX(), y, user.getZ());

			for (Direction direction : Direction.values()) {
				if (!direction.getAxis().isHorizontal()) continue;
				var hit = user.level().clip(new ClipContext(hitStart, hitStart.add(direction.getStepX() * hitOffset, 0, direction.getStepZ() * hitOffset), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, user));

				if (level.getBlockState(hit.getBlockPos()).is(AlchemancyTags.Blocks.MAGNETIC_STICKS_TO)) {
					user.setDeltaMovement(delta.x, Math.max(delta.y, 0), delta.z);
					user.resetFallDistance();
					user.setOnGround(true);
					playParticles(user, y, stack);
					break;
				}
			}
		}

		repelUser(user);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		repelUser(projectile);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		repelUser(itemEntity);
	}

	public static void playParticles(LivingEntity user, double y, ItemStack stack) {
		playParticles(user, y, stack, 4);
	}

	public static void playParticles(Entity user, double y, ItemStack stack, int amount) {
		if (user.level() instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(ParticleTypes.ELECTRIC_SPARK), user.getX(), y, user.getZ(), amount, user.getBbWidth() * 0.45f, 0, user.getBbWidth() * 0.45f, 0);
	}

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {

		magnetize(user.level(), user, user.getEyePosition(), stack);

		if (user.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLE_A), user.getX(), user.getY(0.5f), user.getZ(), 1, user.getBbWidth() * 0.5f, user.getBbHeight() * 0.25f, user.getBbWidth() * 0.5f, 0);
			serverLevel.sendParticles(SparklingProperty.getParticles(stack).orElse(PARTICLE_B), user.getX(), user.getY(0.5f), user.getZ(), 1, user.getBbWidth() * 0.5f, user.getBbHeight() * 0.25f, user.getBbWidth() * 0.5f, 0);
		}
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
		magnetize(root.getLevel(), null, root.getBlockPos().getCenter(), root.getItem());
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
		playRootedParticles(root, randomSource, PARTICLE_A);
		playRootedParticles(root, randomSource, PARTICLE_B);
	}

	private static <E extends Entity> void repelUser(Entity user, Class<E> targetEntities, float strength, Predicate<E> predicate) {
		float radius = RADIUS * 0.25f;
		int count = 0;

		for (E target : user.level().getEntitiesOfClass(targetEntities, CommonUtils.boundingBoxAroundPoint(user.position(), radius), predicate)) {
			if (target.equals(user))
				continue;

			double distanceTo = target.position().distanceTo(user.position());

			float str = (float) Math.max(0, radius - distanceTo) * .05f * strength;

			user.hasImpulse = true;
			Vec3 vec3 = user.getDeltaMovement();
			Vec3 vec31 = target.position().subtract(user.position()).normalize().scale(str);

			user.setDeltaMovement(vec3.subtract(vec31));

			if (target.level() instanceof ServerLevel serverLevel)
				serverLevel.sendParticles(PARTICLE_B, target.getX(), target.getY(0.5f), target.getZ(), 1, target.getBbWidth() * 0.5f, target.getBbHeight() * 0.25f, target.getBbWidth() * 0.25f, 0);
			count++;
		}

		if (count > 0 && user.level() instanceof ServerLevel serverLevel)
			serverLevel.sendParticles(PARTICLE_B, user.getX(), user.getY(0.5f), user.getZ(), count, user.getBbWidth() * 0.5f, user.getBbHeight() * 0.25f, user.getBbWidth() * 0.25f, 0);
	}

	public static void magnetize(Level level, @Nullable Entity user, Vec3 center, ItemStack stack) {
		if (level.isClientSide()) return;
		forEachInRadius(user, level, center, LivingEntity.class, entity -> true, target ->
		{
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				ItemStack stackInSlot = target.getItemBySlot(slot);
				if (canBeMagnetized(stackInSlot)) {
					if (!(target instanceof Player))
						target.move(MoverType.PLAYER, center.subtract(target.position()).normalize().scale(0.05f));
					playParticles(target, target.getRandomY(), stack, 1);

					if (level.getRandom().nextFloat() < 0.005f) {
						if (target instanceof Player player)
							player.drop(stackInSlot, true);
						else HollowProperty.nonPlayerDrop(target, stackInSlot, false, true);
						target.setItemSlot(slot, ItemStack.EMPTY);
					}
				}
			}
		});
		pullEntities(user, stack, level, center, ItemEntity.class, itemEntity -> canBeMagnetized(itemEntity.getItem()), ItemEntity::setNoPickUpDelay);
		pullEntities(user, stack, level, center, AbstractArrow.class, arrow -> canBeMagnetized(arrow.getPickupItemStackOrigin()), arrow -> {
			if (!level.isClientSide() && arrow.pickup == AbstractArrow.Pickup.ALLOWED && user instanceof Player player) {
				((AbstractArrowAccessor) arrow).setInGround(true);
				arrow.playerTouch(player);
			}
		});
		pullEntities(user, stack, level, center, Projectile.class, entity -> entity instanceof ItemSupplier supplier && canBeMagnetized(supplier.getItem()), projectile -> {

			ItemEntity droppedItem = new ItemEntity(projectile.level(), projectile.position().x, projectile.position().y, projectile.position().z, stack.copy());
			droppedItem.setNoPickUpDelay();
			projectile.level().addFreshEntity(droppedItem);
			projectile.discard();
		});
		pullEntities(user, stack, level, center, LivingEntity.class, entity -> entity.getType().is(AlchemancyTags.EntityTypes.PULLED_IN_BY_MAGNETIC), e -> {
		}, 0.075f);

	}

	public static void repelUser(Entity user) {
		repelUser(user, LivingEntity.class, 0.5f, target -> {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (InfusedPropertiesHelper.hasProperty(target.getItemBySlot(slot), AlchemancyProperties.MAGNETIC))
					return true;
			}
			return false;
		});

		repelUser(user, Projectile.class, 0.25f, target -> InfusedPropertiesHelper.hasProperty(PropertyEventHandler.getProjectileItemStack(target), AlchemancyProperties.MAGNETIC));
	}

	private static <E extends Entity> int forEachInRadius(@Nullable Entity user, Level level, Vec3 center, Class<E> targetClass, Predicate<E> targetCondition, Consumer<E> consumer) {

		int count = 0;
		for (E target : level.getEntitiesOfClass(targetClass, CommonUtils.boundingBoxAroundPoint(center, RADIUS), targetCondition)) {
			if (target == user) continue;
			consumer.accept(target);
			count++;
		}
		return count;
	}

	private static <E extends Entity> int pullEntities(@Nullable Entity user, ItemStack stack, Level level, Vec3 center, Class<E> targetClass, Predicate<E> targetCondition, Consumer<E> whenCloseEnough) {
		return pullEntities(user, stack, level, center, targetClass, targetCondition, whenCloseEnough, 1);
	}

	private static <E extends Entity> int pullEntities(@Nullable Entity user, ItemStack stack, Level level, Vec3 center, Class<E> targetClass, Predicate<E> targetCondition, Consumer<E> whenCloseEnough, float strength) {
		return forEachInRadius(user, level, center, targetClass, targetCondition, target -> {
			double distanceTo = target.position().distanceTo(center);

			if (distanceTo < (user == null ? 0.5 : user.getBbWidth() * 0.5 + 1))
				whenCloseEnough.accept(target);

			float str = (float) Math.max(0, RADIUS - distanceTo) * .05f * strength;

			if (target instanceof AbstractArrowAccessor arrow)
				arrow.setInGround(false);

			Vec3 vec3 = target.getDeltaMovement();
			Vec3 vec31 = target.position().subtract(center).normalize().scale(str);

			target.setDeltaMovement(vec3.scale(1 - 0.5 * (1 - distanceTo / RADIUS)).subtract(vec31));
			playParticles(target, target.getRandomY(), stack, 1);

			target.hasImpulse = true;
			target.hurtMarked = true;
		});
	}

	public static boolean canBeMagnetized(ItemStack stack) {
		return InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.AFFECTED_BY_MAGNETIC) ||
				AlchemancyProperties.getDormantProperties(stack).stream().anyMatch(p -> p.is(AlchemancyTags.Properties.AFFECTED_BY_MAGNETIC));
	}

	@Override
	public Component getDisplayText(ItemStack stack) {
		return getName(stack);
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(getLanguageKey() + ".format", Component.translatable(getLanguageKey() + ".a").withColor(0xFF0000), Component.translatable(getLanguageKey() + ".b").withColor(0x0000FF));
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);

	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return Optional.of(UseAnim.BOW);
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.flashColorsOverTime(1000, 0xFF0000, 0x0000FF);
	}

}
