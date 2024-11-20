package net.cibernet.alchemancy.properties;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class BrushProperty extends Property
{
	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result)
	{
		return Math.max(result, 200);
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(!event.isCanceled())
		{
			event.getEntity().startUsingItem(event.getHand());
			event.setCancellationResult(InteractionResult.CONSUME);
			event.setCanceled(true);
		}
	}

	@Override
	public void onItemUseTick(LivingEntity user, ItemStack stack, LivingEntityUseItemEvent.Tick event)
	{
		Items.BRUSH.onUseTick(user.level(), user, stack, event.getDuration());
	}

	@Override
	public boolean modifyAcceptAbility(ItemStack stack, ItemAbility itemAbility, boolean original, boolean result)
	{
		return result || ItemAbilities.DEFAULT_BRUSH_ACTIONS.contains(itemAbility);
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return Optional.of(UseAnim.BRUSH);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xEEC39A;
	}
}
