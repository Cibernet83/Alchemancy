package net.cibernet.alchemancy.blocks;

import net.cibernet.alchemancy.client.particle.SparkParticle;
import net.cibernet.alchemancy.properties.GlowRingProperty;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlowingOrbBlock extends BarrierBlock {
	private static final VoxelShape SHAPE = Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	public GlowingOrbBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
		Vec3 center = pos.getCenter();
		RandomSource random = level.getRandom();

		for (int i = 0; i < 8; i++) {
			level.addParticle(GlowRingProperty.PARTICLES,
					center.x() + (random.nextFloat() - 0.5f) * 0.3f,
					center.y() + (random.nextFloat() - 0.5f) * 0.3f,
					center.z() + (random.nextFloat() - 0.5f) * 0.3f,
					(random.nextFloat() - 0.5f) * 0.6f,
					(random.nextFloat() - 0.5f) * 0.3f,
					(random.nextFloat() - 0.5f) * 0.6f
			);
		}

		level.playLocalSound(pos,
				AlchemancySoundEvents.GLOWING_ORB_EXTINGUISH.value(), SoundSource.BLOCKS,
				0.5F,
				2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F,
				false
		);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Vec3 center = pos.getCenter();
		level.addParticle(GlowRingProperty.PARTICLES,
				center.x + ((random.nextFloat() - 0.5f) * 0.5f),
				center.y + ((random.nextFloat() - 0.5f) * 0.5f),
				center.z + ((random.nextFloat() - 0.5f) * 0.5f),
				0, 0, 0);
	}
}
