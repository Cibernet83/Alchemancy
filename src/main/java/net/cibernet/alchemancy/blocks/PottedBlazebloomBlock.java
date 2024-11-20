package net.cibernet.alchemancy.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;

public class PottedBlazebloomBlock extends FlowerPotBlock
{
	public PottedBlazebloomBlock(DeferredBlock<? extends FlowerBlock> flower, Properties properties)
	{
		super(() -> (FlowerPotBlock) Blocks.FLOWER_POT, flower, properties);
		((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(flower.getId(), () -> this);
	}

	private static final AABB PARTICLE_AREA = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0).bounds();

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
	{

		AABB shape = PARTICLE_AREA.move(0, getShape(state, level, pos, CollisionContext.empty()).bounds().maxY, 0);

		double d0 = pos.getX() + shape.minX;
		double d1 = pos.getY() + shape.minY;
		double d2 = pos.getZ() + shape.minZ;

		double d5 = random.nextDouble() * shape.getXsize();
		double d6 = random.nextDouble() * shape.getYsize();
		double d7 = random.nextDouble() * shape.getZsize();
		level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	}
}
