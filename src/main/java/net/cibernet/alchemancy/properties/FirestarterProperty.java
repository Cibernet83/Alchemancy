package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import java.util.List;
import java.util.Set;

public class FirestarterProperty extends ToolProperty
{
	public FirestarterProperty(int color, TagKey<Block> allowedBlocks, Set<ItemAbility> abilities) {
		super(color, allowedBlocks, abilities);
	}

	public FirestarterProperty(int color, List<RuleFunc> toolRules, Set<ItemAbility> abilities) {
		super(color, toolRules, abilities);
	}

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult)
	{
		return InfusionPropertyDispenseBehavior.executeItemBehavior(blockSource, stack, Items.FLINT_AND_STEEL);
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event)
	{
		Player player = event.getPlayer();
		Level level = event.getLevel();
		BlockPos blockpos = event.getPos();
		BlockState blockstate = level.getBlockState(blockpos);
		UseOnContext context = event.getUseOnContext();

		BlockState blockstate2 = blockstate.getToolModifiedState(context, net.neoforged.neoforge.common.ItemAbilities.FIRESTARTER_LIGHT, false);

		if (blockstate2 == null) {
			BlockPos blockpos1 = blockpos.relative(context.getClickedFace());
			if (BaseFireBlock.canBePlacedAt(level, blockpos1, context.getHorizontalDirection())) {
				level.playSound(player, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
				level.setBlock(blockpos1, blockstate1, 11);
				level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
				ItemStack itemstack = context.getItemInHand();
				if (player instanceof ServerPlayer) {
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos1, itemstack);
					itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
				}

				event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide));
				event.setCanceled(true);
			}
		} else super.onRightClickBlock(event);
	}

}
