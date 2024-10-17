package net.cibernet.alchemancy.properties;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

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
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Player player = event.getEntity();
		Level level = event.getLevel();
		BlockPos blockpos = event.getPos();
		BlockState blockstate = level.getBlockState(blockpos);
		UseOnContext context = new UseOnContext(player, event.getHand(), event.getHitVec());

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


			}
		} else super.onRightClickBlock(event);
	}

}
