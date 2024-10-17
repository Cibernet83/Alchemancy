package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.blocks.EssenceInjectorBlock;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EssenceInjectorBlockEntity extends BlockEntity implements IEssenceHolder
{
	public final EssenceContainer essence = new EssenceContainer(1000);

	public EssenceInjectorBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(AlchemancyBlockEntities.ESSENCE_INJECTOR.get(), pos, blockState);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EssenceInjectorBlockEntity blockEntity)
	{
		for (Direction direction : Direction.values())
		{
			if(!state.getValue(EssenceInjectorBlock.FACING).equals(direction) && level.getBlockEntity(pos.relative(direction)) instanceof IEssenceHolder holder && holder.canTransferFromDirection(direction))
				holder.getEssenceContainer().transferTo(blockEntity.getEssenceContainer(), 100, false);
		}
	}

	@Override
	public EssenceContainer getEssenceContainer() {
		return essence;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		tag.put("essence", essence.saveToTag(new CompoundTag()));
	}


	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		essence.loadFromTag(tag.getCompound("essence"));
	}

}
