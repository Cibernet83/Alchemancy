package net.cibernet.alchemancy.properties;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Optional;

public class BowProperty extends Property
{
	@Override
	public void onStopUsingItem(ItemStack stack, LivingEntity user, LivingEntityUseItemEvent.Stop event)
	{
		if(event.getDuration() > 5)
		{
			Items.BOW.asItem().releaseUsing(new ItemStack(Items.BOW), user.level(), user, event.getDuration());
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled() && canUse(event.getLevel(), event.getEntity(), event.getHand()))
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.CONSUME);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

		if(!event.isCanceled() && canUse(event.getLevel(), event.getEntity(), event.getHand()))
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.CONSUME);
			event.setCanceled(true);
		}
	}

	public boolean canUse(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = new ItemStack(Items.BOW);
		boolean flag = !player.getProjectile(itemstack).isEmpty();

		InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(itemstack, level, player, hand, flag);
		if (ret != null) return ret.getResult().consumesAction();

		return player.hasInfiniteMaterials() || flag;
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return current.isEmpty() ? Optional.of(UseAnim.BOW) : current;
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x8621A8;
	}

	@Override
	public int getPriority() {
		return Priority.LOW;
	}
}
