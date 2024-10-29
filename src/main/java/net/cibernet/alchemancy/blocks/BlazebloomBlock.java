package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlazebloomBlock extends FlowerBlock
{
	public BlazebloomBlock(Holder<MobEffect> effect, float seconds, Properties properties) {
		super(effect, seconds, properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(AlchemancyTags.Blocks.SUPPORTS_BLAZEBLOOM);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
	{
		AABB shape = getShape(state, level, pos, CollisionContext.empty()).bounds();

		double d0 = pos.getX() + shape.minX;
		double d1 = pos.getY() + shape.minY;
		double d2 = pos.getZ() + shape.minZ;

		double d5 = random.nextDouble() * shape.getXsize();
		double d6 = random.nextDouble() * shape.getYsize();
		double d7 = random.nextDouble() * shape.getZsize();
		level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
	}
}
