package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.blocks.blockentities.AlchemancyCatalystBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyCriteriaTriggers;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlchemancyForgeBlock extends InfusionPedestalBlock
{

	public AlchemancyForgeBlock(Properties properties) {
		super(properties);
	}

	private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.9375, 1);

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		BlockPos targetPos = pos.above(2);

		if(level.getBlockState(targetPos).is(AlchemancyTags.Blocks.ALCHEMANCY_CRYSTAL_CATALYSTS))
		{
			level.destroyBlock(targetPos, false);
			level.setBlockAndUpdate(targetPos, AlchemancyBlocks.ALCHEMANCY_CATALYST.get().defaultBlockState());

			if(!level.isClientSide && level.getBlockEntity(targetPos) instanceof AlchemancyCatalystBlockEntity catalyst)
			{
				catalyst.playAnimation(true);
				if(player instanceof ServerPlayer serverPlayer)
					AlchemancyCriteriaTriggers.ACTIVATE_FORGE.get().trigger(serverPlayer, pos);
			}

			return ItemInteractionResult.SUCCESS;
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
}
