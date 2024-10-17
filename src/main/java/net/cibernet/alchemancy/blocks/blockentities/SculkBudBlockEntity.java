package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;

public class SculkBudBlockEntity extends BlockEntity
{
	private final SculkCatalystBlockEntity.CatalystListener sculkListener;

	public SculkBudBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(AlchemancyBlockEntities.SCULK_BUD.get(), pos, blockState);
		this.sculkListener = new SculkCatalystBlockEntity.CatalystListener(blockState, new BlockPositionSource(pos));
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SculkBudBlockEntity bud) {
		bud.sculkListener.getSculkSpreader().updateCursors(level, pos, level.getRandom(), true);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.sculkListener.getSculkSpreader().load(tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		this.sculkListener.getSculkSpreader().save(tag);
		super.saveAdditional(tag, registries);
	}

	public void addCursor(int charge, BlockPos pos)
	{
		sculkListener.getSculkSpreader().addCursors(pos, charge);
	}
}
