package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class GustBasketBlock extends DirectionalBlock {

	public static final MapCodec<GustBasketBlock> CODEC = simpleCodec(GustBasketBlock::new);

	private static final float DISTANCE = 6;

	public GustBasketBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

		Direction facing = state.getValue(FACING);
		int amount = random.nextInt(5);
		for (int i = 0; i < amount; i++)
		{

			double xOff = facing.getStepX() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepX());
			double yOff = facing.getStepY() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepY());
			double zOff = facing.getStepZ() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepZ());
			level.addParticle(ParticleTypes.SMALL_GUST,
					pos.getX() + xOff + Math.max(0, facing.getStepX()),
					pos.getY() + yOff + Math.max(0, facing.getStepY()),
					pos.getZ() + zOff + Math.max(0, facing.getStepZ()),
					0, 0, 0);
		}
	}

	private static final HashMap<ResourceKey<Level>, ArrayList<BlockPos>> PARTICLE_PROCESSED_GUST_BASKETS = new HashMap<>();

	public static void doWindyThing(Entity entity)
	{
		Level level = entity.level();

		BlockPos.betweenClosedStream(CommonUtils.boundingBoxAroundPoint(entity.position(), DISTANCE)).filter(pos -> level.getBlockState(pos).is(AlchemancyBlocks.GUST_BASKET)).forEach(pos ->
		{
			BlockState state = level.getBlockState(pos);
			var facing = state.getValue(FACING);
			Vec3 facingStep = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

			var clipPos = level.clip(new ClipContext(pos.getCenter().add(facingStep.scale(0.5f)), pos.getCenter().add(facingStep.scale(DISTANCE + 0.5f)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getBlockPos();
			double distance = pos.distSqr(clipPos);
			distance = Math.sqrt(distance);

			if(new AABB(pos).expandTowards(facingStep.scale(distance - 1)).intersects(entity.getBoundingBox()))
			{
				var oldDelta = entity.getDeltaMovement();
				var newDelta = oldDelta.add(facingStep.scale((1 - entity.position().distanceTo(pos.getCenter()) / DISTANCE) * 0.25f));

				entity.setDeltaMovement(newDelta);
				entity.hasImpulse = true;
				entity.fallDistance = Math.max(0, entity.fallDistance - 2);

				if(level.isClientSide() && !(PARTICLE_PROCESSED_GUST_BASKETS.containsKey(level.dimension()) && PARTICLE_PROCESSED_GUST_BASKETS.get(level.dimension()).contains(pos)))
				{
					RandomSource random = level.getRandom();
					double speed = distance / 6f;
					int amount = random.nextInt(5);
					for (int i = 0; i < amount; i++)
					{
						double xOff = random.nextDouble() * (1 - Math.abs(facing.getStepX()));
						double yOff = random.nextDouble() * (1 - Math.abs(facing.getStepY()));
						double zOff = random.nextDouble() * (1 - Math.abs(facing.getStepZ()));
						level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + xOff + facing.getStepX(), pos.getY() + yOff + facing.getStepY(), pos.getZ() + zOff + facing.getStepZ(),
								facing.getStepX() * speed, facing.getStepY() * speed, facing.getStepZ() * speed);
					}


					if(random.nextFloat() > 0.15f)
						level.playLocalSound(pos, SoundEvents.BREEZE_SLIDE, SoundSource.BLOCKS, 0.25f, (float) (distance / DISTANCE),false);

					//this is probably the worst solution ever >_>
					if(!PARTICLE_PROCESSED_GUST_BASKETS.containsKey(level.dimension()))
						PARTICLE_PROCESSED_GUST_BASKETS.put(level.dimension(), new ArrayList<>());
					PARTICLE_PROCESSED_GUST_BASKETS.get(level.dimension()).add(new BlockPos(pos));
				}

			}
		});
	}

	public static void resetParticleTrackers(Level level) {
		if(level.isClientSide() && PARTICLE_PROCESSED_GUST_BASKETS.containsKey(level.dimension()))
			PARTICLE_PROCESSED_GUST_BASKETS.get(level.dimension()).clear();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}
}
