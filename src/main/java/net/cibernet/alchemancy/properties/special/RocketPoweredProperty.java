package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.Random;

public class RocketPoweredProperty extends Property
{
	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event)
	{
		if(event.getDuration() % 20 == 0)
		{
			EquipmentSlot slot = user.getUsedItemHand() == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
			if(stack.isDamageableItem())
				stack.hurtAndBreak(1, user, slot);
			else consumeItem(user, stack, slot);
		}

		playParticles(user);
		user.moveRelative(0.25f, new Vec3(0, (float)Math.cos((user.getXRot()+90)*Math.PI/180f), (float)Math.sin((user.getXRot()+90)*Math.PI/180f)));
		user.hasImpulse = true;
	}

	public static void playParticles(Entity source)
	{
		Vec3 pos = source.position();
		RandomSource randomSource = source.getRandom();
		source.level().addParticle(
				ParticleTypes.FLAME,
				pos.x(),
				pos.y(),
				pos.z(),
				-source.getDeltaMovement().x * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().y * 0.5 + randomSource.nextGaussian() * 0.05,
				-source.getDeltaMovement().z * 0.5 + randomSource.nextGaussian() * 0.05
		);

	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);

	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current)
	{
		return current.isEmpty() && original == UseAnim.NONE ? Optional.of(UseAnim.BOW) : current;
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result)
	{
		return 72000;
	}

	@Override
	public Component getName(ItemStack stack) {
		return super.getName(stack).copy().withStyle(ChatFormatting.BOLD);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD62A2A;
	}
}
