package net.cibernet.alchemancy.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlowingOrbBlock  extends BarrierBlock
{
	private static final VoxelShape SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	/* TODO
	 * Sparkle Particles
	 * Fire Extinguish sounds when broken
	 */
	public GlowingOrbBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL; //TODO use cube until we figure out a good way to use particles
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
	{
		//Vec3 center = pos.getCenter();
		//level.addParticle(AlchemancyParticles.GLOWING_ORB.get(), center.x, center.y, center.z, 0, 0, 0);
	}
}
