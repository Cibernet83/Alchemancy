package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.events.handler.GeneralEventHandler;
import net.cibernet.alchemancy.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

public class FlatHopperBlock extends DirectionalBlock {

	private static final TreeMap<Direction, VoxelShape> SHAPES = VoxelShapeUtils.createDirectionMap(Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 3.0D, 1.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 3.0D),
			Block.box(13.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D),
			Block.box(0.0D, 0.0D, 13.0D, 16.0D, 1.0D, 16.0D)
	));

	public FlatHopperBlock(Properties properties) {
		super(properties);

		GeneralEventHandler.registerTickingBlockFunction(this, FlatHopperBlock::tick);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}


	public static void tick(ServerLevel level, BlockPos pos) {
		Direction facing = level.getBlockState(pos).getValue(FACING).getOpposite();
		BlockPos connectedPos = pos.relative(facing);
		BlockState connectedState = level.getBlockState(connectedPos);

		var cap = level.getCapability(Capabilities.ItemHandler.BLOCK, connectedPos, connectedState, level.getBlockEntity(pos), facing.getOpposite());
		if(cap == null) return;

		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos))) {
			if((item.hasPickUpDelay() && !item.getPersistentData().getBoolean(Alchemancy.MODID + ":from_pedestal")) ||
					item.getPersistentData().getBoolean(Alchemancy.MODID + ":from_pedestal_click"))
				continue;
			if(ItemHandlerHelper.insertItem(cap, item.getItem(), false).isEmpty())
				item.discard();
			return;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	public static final MapCodec<FlatHopperBlock> CODEC = simpleCodec(FlatHopperBlock::new);

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}
}
