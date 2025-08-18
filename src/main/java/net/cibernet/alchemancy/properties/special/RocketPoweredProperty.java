package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class RocketPoweredProperty extends Property {
	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event) {
		if (event.getDuration() % 20 == 6) {
			EquipmentSlot slot = user.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
			if (stack.isDamageableItem())
				stack.hurtAndBreak(2, user, slot);
			else consumeItem(user, stack, slot);
		}

		playParticles(user, stack);
		user.moveRelative(user.isFallFlying() ? 0.05f : 0.2f, new Vec3(0, (float) Math.cos((user.getXRot() + 90) * Math.PI / 180f), (float) Math.sin((user.getXRot() + 90) * Math.PI / 180f)));

		if (!user.isFallFlying() && !(user instanceof Player player && player.getAbilities().flying))
			user.setDeltaMovement(user.getDeltaMovement().add(0, user.getGravity() * 0.75f, 0));

		user.hasImpulse = true;
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if (slot == EquipmentSlot.FEET && !user.isPassenger()) {
			if (((LivingEntityAccessor) user).isJumping()) {
				if (!user.level().isClientSide()) {
					if (user.tickCount % 20 == 0) {
						if (stack.isDamageableItem())
							stack.hurtAndBreak(2, user, slot);
						else consumeItem(user, stack, slot);
					}
				}

				playBootParticles(user, stack);
				user.moveRelative(0.125f, user.isFallFlying() ?
						new Vec3(0, (float) Math.cos((user.getXRot() + 90) * Math.PI / 180f), (float) Math.sin((user.getXRot() + 90) * Math.PI / 180f)) :
						new Vec3(0, 1, 0));

				user.fallDistance *= 0.8f;

				user.hasImpulse = true;

			}
		}
	}

	public static ParticleOptions getParticles(ItemStack stack) {
		return SparklingProperty.getParticles(stack).orElse(stack.is(AlchemancyItems.BARRELS_WARHAMMER) ?
				AlchemancyParticles.WARHAMMER_FLAME.get() : ParticleTypes.FLAME);
	}

	public static void playParticles(Entity source, ItemStack stack) {
		Vec3 pos = source.position();
		RandomSource randomSource = source.getRandom();
		source.level().addParticle(
				getParticles(stack),
				pos.x() - Math.cos((source.getXRot() + 90) * Math.PI / 180f) * 0.2f,
				source.getEyeY() - 0.2f,
				pos.z() - Math.sin((source.getXRot() + 90) * Math.PI / 180f) * 0.2f,
				-source.getDeltaMovement().x * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().y * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().z * 0.5 + randomSource.nextGaussian() * 0.05
		);

	}

	public static void playBootParticles(Entity source, ItemStack stack) {
		Vec3 pos = source.position();
		RandomSource randomSource = source.getRandom();
		source.level().addParticle(
				getParticles(stack),
				pos.x(),
				pos.y(),
				pos.z(),
				-source.getDeltaMovement().x * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().y * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().z * 0.5 + randomSource.nextGaussian() * 0.05
		);

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
	public Component getDisplayText(ItemStack stack) {
		return super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD62A2A;
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}
}
