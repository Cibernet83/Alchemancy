package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AlchemancyForgeBlock extends InfusionPedestalBlock
{
	public AlchemancyForgeBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		//TODO make it use essense to initiate
		BlockPos targetPos = pos.above(2);

		if(level.getBlockState(targetPos).canBeReplaced())
		{
			level.destroyBlock(targetPos, true);
			level.setBlockAndUpdate(targetPos, AlchemancyBlocks.ALCHEMANCY_CATALYST.get().defaultBlockState());
			level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1, 1);
			return ItemInteractionResult.SUCCESS;
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
}
