package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.blocks.blockentities.SculkBudBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SculkBudBlock extends BaseEntityBlock implements SculkBehaviour
{
	public static final MapCodec<SculkBudBlock> CODEC = simpleCodec(SculkBudBlock::new);
	private final IntProvider xpRange = ConstantInt.of(5);

	protected static final VoxelShape AABB = Block.box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0);

	public SculkBudBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelAccessor level, BlockPos pos,
	                      @org.jetbrains.annotations.Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
	                      @org.jetbrains.annotations.Nullable net.minecraft.world.entity.Entity breaker, ItemStack tool) {
		return this.xpRange.sample(level.getRandom());
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.below();
		return canSupportRigidBlock(level, blockpos) || canSupportCenter(level, blockpos, Direction.UP);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SculkBudBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide() ? null : createTickerHelper(blockEntityType, AlchemancyBlockEntities.SCULK_BUD.get(), SculkBudBlockEntity::serverTick);
	}

	@Override
	public int attemptUseCharge(SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos pos, RandomSource random, SculkSpreader spreader, boolean shouldConvertBlocks)
	{
		return shouldConvertBlocks && attemptPlaceSculk(spreader, level, pos.below(), random) ? cursor.getCharge()-1 : 0;
	}

	private boolean attemptPlaceSculk(SculkSpreader spreader, LevelAccessor level, BlockPos pos, RandomSource random)
	{
		TagKey<Block> tagkey = spreader.replaceableBlocks();
		BlockState blockstate1 = level.getBlockState(pos);

		if (blockstate1.is(tagkey)) {
			BlockState blockstate2 = Blocks.SCULK.defaultBlockState();
			level.setBlock(pos, blockstate2, 3);
			Block.pushEntitiesUp(blockstate1, blockstate2, level, pos);
			level.playSound(null, pos, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);

			for (Direction direction2 : Direction.values()) {
				BlockPos blockpos1 = pos.relative(direction2);
				BlockState blockstate3 = level.getBlockState(blockpos1);
				if (blockstate3.is(this)) {
					this.onDischarged(level, blockstate3, blockpos1, random);
				}

			}
			return true;
		}

		return false;
	}
}
