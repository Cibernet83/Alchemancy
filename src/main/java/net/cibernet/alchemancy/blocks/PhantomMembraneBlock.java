package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.entity.CustomFallingBlock;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PhantomMembraneBlock extends ColoredFallingBlock {

	public PhantomMembraneBlock(Properties properties) {
		super(new ColorRGBA(0x7E627B), properties);
	}

	@Override
	public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity fallingBlock) {
		if (!fallingBlock.isSilent()) {
			level.levelEvent(2008, pos, 0);
			level.levelEvent(2001, pos, getId(defaultBlockState()));
			fallingBlock.playSound(AlchemancySoundEvents.PHANTOM_MEMBRANE_POP.value());
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (isFree(level.getBlockState(pos.above())) && pos.getY() <= level.getMaxBuildHeight()) {
			CustomFallingBlock fallingblockentity = CustomFallingBlock.fall(level, pos, state, asItem().getDefaultInstance());
			fallingblockentity.setGravity(-0.0125f);
			fallingblockentity.setHasCollision(true);
			fallingblockentity.dropItem = false;
			this.falling(fallingblockentity);
		}
	}
}
