package net.cibernet.alchemancy.blocks;

import com.mojang.serialization.MapCodec;
import net.cibernet.alchemancy.events.handler.GeneralEventHandler;
import net.cibernet.alchemancy.network.S2CAddPlayerMovementPayload;
import net.cibernet.alchemancy.network.S2CPlayGustBasketEffectsPayload;
import net.cibernet.alchemancy.properties.special.GustJetProperty;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;

@EventBusSubscriber(Dist.CLIENT)
public class GustBasketBlock extends DirectionalBlock {

	public static final MapCodec<GustBasketBlock> CODEC = simpleCodec(GustBasketBlock::new);

	private static final float DISTANCE = 6;


	private static final TreeMap<Direction, VoxelShape> SHAPES = VoxelShapeUtils.createDirectionMap(Shapes.or(
			Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
			Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D)
	));

	public GustBasketBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));

		GeneralEventHandler.registerTickingBlockFunction(this, GustBasketBlock::tick);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {

		Direction facing = state.getValue(FACING);
		int amount = random.nextInt(5);
		for (int i = 0; i < amount; i++) {

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

	public static void tick(ServerLevel level, BlockPos pos) {

		BlockState state = level.getBlockState(pos);
		var facing = state.getValue(FACING);
		Vec3 facingStep = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

		var offset = facingStep.scale(0.5f);
		Vec3 startVec = pos.getCenter().add(offset.x(), offset.y(), offset.z());

		//FIXME: figure out a smart way to clip 4 more times, one for each corner
		var clipPos = level.clip(new ClipContext(startVec, startVec.add(facingStep.scale(DISTANCE)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getBlockPos();
		double distance = pos.distSqr(clipPos);
		if (distance <= 0) return;

		distance = Math.min(DISTANCE, Math.sqrt(distance));
		boolean playEffects = false;

		for (Entity target : level.getEntities(null, new AABB(pos).expandTowards(facingStep.scale(distance - 1)))) {
			playEffects = true;

			var movement = facingStep.scale((1 - target.position().distanceTo(pos.getCenter()) / DISTANCE) * 0.25f);

			if (facing != Direction.UP && target instanceof ServerPlayer player) {
				PacketDistributor.sendToPlayer(player, new S2CAddPlayerMovementPayload(movement));
			}
			target.setDeltaMovement(target.getDeltaMovement().add(movement));

			target.hasImpulse = true;
			target.fallDistance = Math.max(0, target.fallDistance - 2);
		}

		if (playEffects)
			PacketDistributor.sendToPlayersTrackingChunk(level, level.getChunk(pos).getPos(), new S2CPlayGustBasketEffectsPayload(pos, distance));
	}

	public static void clientPlayerTick(Player player) {

		if(player.isSpectator()) return;

		Level level = player.level();
		BlockPos.betweenClosedStream(CommonUtils.boundingBoxAroundPoint(player.position(), player.getBbWidth() * 0.45f).expandTowards(0, -DISTANCE, 0)).forEach(pos -> {
			BlockState state = level.getBlockState(pos);
			if(!(state.is(AlchemancyBlocks.GUST_BASKET) && state.getValue(FACING) == Direction.UP))
				return;

			var facing = Direction.UP;
			Vec3 facingStep = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

			var offset = facingStep.scale(0.5f);
			Vec3 startVec = pos.getCenter().add(offset.x(), offset.y(), offset.z());

			//FIXME: figure out a smart way to clip 4 more times, one for each corner
			var clipPos = level.clip(new ClipContext(startVec, startVec.add(facingStep.scale(DISTANCE)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty())).getBlockPos();
			float distance = Mth.sqrt((float) pos.distSqr(clipPos));
			if (pos.getY() + distance < player.getY()) return;

			var movement = facingStep.scale((1 - player.position().distanceTo(pos.getCenter()) / DISTANCE) * 0.25f);
			player.setDeltaMovement(player.getDeltaMovement().add(movement));
			player.hasImpulse = true;
		});
	}

	private static int gustSounds = 0;

	@SubscribeEvent
	private static void resetGustSounds(ClientTickEvent.Post event) {
		gustSounds = 0;
	}

	public static void playGustEffects(Level level, BlockPos pos, double distance) {

		RandomSource random = level.getRandom();
		var facing = level.getBlockState(pos).getValue(FACING);

		double speed = 0.33f;
		int amount = random.nextInt(5);
		for (int i = 0; i < amount; i++) {
			double xOff = facing.getStepX() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepX());
			double yOff = facing.getStepY() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepY());
			double zOff = facing.getStepZ() < 0 ? 0 : random.nextDouble() * (1 - facing.getStepZ());
			level.addParticle(GustJetProperty.PARTICLES,
					pos.getX() + xOff + Math.max(0, facing.getStepX()),
					pos.getY() + yOff + Math.max(0, facing.getStepY()),
					pos.getZ() + zOff + Math.max(0, facing.getStepZ()),
					facing.getStepX() * speed, facing.getStepY() * speed, facing.getStepZ() * speed);
		}

		if (random.nextFloat() < 0.15f && gustSounds <= 2)
		{
			level.playLocalSound(pos, AlchemancySoundEvents.GUST_BASKET.value(), SoundSource.BLOCKS, 0.25f, (float) (distance / DISTANCE), false);
			gustSounds++;
		}
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
