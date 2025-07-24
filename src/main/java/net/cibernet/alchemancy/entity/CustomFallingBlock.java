package net.cibernet.alchemancy.entity;

import com.mojang.logging.LogUtils;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.accessors.FallingBlockEntityAccessor;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.List;

public class CustomFallingBlock extends FallingBlockEntity
{
	private static final Logger LOGGER = LogUtils.getLogger();

	private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(
			CustomFallingBlock.class, EntityDataSerializers.ITEM_STACK
	);

	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> HAS_COLLISION = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.BOOLEAN);

	public CustomFallingBlock(EntityType<? extends FallingBlockEntity> entityType, Level level) {
		super(entityType, level);
	}

	private CustomFallingBlock(Level level, double x, double y, double z, BlockState state, ItemStack item) {
		this(AlchemancyEntities.FALLING_BLOCK.get(), level);
		((FallingBlockEntityAccessor)this).setBlockState(state);
		this.blocksBuilding = true;
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
		setItem(item);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(GRAVITY, 0.04f);
		builder.define(HAS_COLLISION, false);
		builder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
	}



	public void setItem(ItemStack stack) {
		this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
	}

	public ItemStack getItem() {
		return this.getEntityData().get(DATA_ITEM_STACK);
	}

	@Override
	public void move(MoverType type, Vec3 pos)
	{
		super.move(type, pos);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	protected void applyGravity() {
		super.applyGravity();
	}

	@Override
	public void tick() {

		if (this.blockState.isAir()) {
			this.discard();
		} else {
			Block block = this.blockState.getBlock();
			this.time++;
			this.applyGravity();
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.handlePortal();
			if (!this.level().isClientSide && (this.isAlive() || this.forceTickAfterTeleportToDuplicate)) {
				BlockPos blockpos = this.blockPosition();
				boolean isConcretePowder = this.blockState.getBlock() instanceof ConcretePowderBlock;
				boolean canBeHydrated = isConcretePowder && this.blockState.canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockpos), blockpos);
				double d0 = this.getDeltaMovement().lengthSqr();
				if (isConcretePowder && d0 > 1.0) {
					BlockHitResult blockhitresult = this.level()
							.clip(
									new ClipContext(
											new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this
									)
							);
					if (blockhitresult.getType() != HitResult.Type.MISS && this.blockState.canBeHydrated(this.level(), blockpos, this.level().getFluidState(blockhitresult.getBlockPos()), blockhitresult.getBlockPos())) {
						blockpos = blockhitresult.getBlockPos();
						canBeHydrated = true;
					}
				}

				if (verticalCollision || canBeHydrated) {
					BlockState blockstate = this.level().getBlockState(blockpos);
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
					if (!blockstate.is(Blocks.MOVING_PISTON)) {
						if (!this.cancelDrop) {
							boolean canReplace = blockstate.canBeReplaced(
									new DirectionalPlaceContext(this.level(), blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)
							);
							boolean canPlace = isFree() && (!isConcretePowder || !canBeHydrated);
							boolean canPlacedSurvive = this.blockState.canSurvive(this.level(), blockpos) && !canPlace;
							if (canReplace && canPlacedSurvive) {
								if (this.blockState.hasProperty(BlockStateProperties.WATERLOGGED)
										&& this.level().getFluidState(blockpos).getType() == Fluids.WATER) {
									this.blockState = this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
								}

								if (this.level().setBlock(blockpos, this.blockState, 3)) {
									((ServerLevel)this.level())
											.getChunkSource()
											.chunkMap
											.broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level().getBlockState(blockpos)));
									this.discard();
									if (block instanceof Fallable) {
										((Fallable)block).onLand(this.level(), blockpos, this.blockState, blockstate, this);
									}

									if (this.blockData != null && this.blockState.hasBlockEntity()) {
										BlockEntity blockentity = this.level().getBlockEntity(blockpos);
										if (blockentity != null) {
											CompoundTag compoundtag = blockentity.saveWithoutMetadata(this.level().registryAccess());

											for (String s : this.blockData.getAllKeys()) {
												compoundtag.put(s, this.blockData.get(s).copy());
											}

											try {
												blockentity.loadWithComponents(compoundtag, this.level().registryAccess());
											} catch (Exception exception) {
												LOGGER.error("Failed to load block entity from falling block", (Throwable)exception);
											}

											blockentity.setChanged();
										}
									}
								} else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									this.discard();
									this.callOnBrokenAfterFall(block, blockpos);
									dropItem();
								}
							} else {
								this.discard();
								this.callOnBrokenAfterFall(block, blockpos);
								if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
									dropItem();
								}
							}
						} else {
							this.discard();
							this.callOnBrokenAfterFall(block, blockpos);
						}
					}
				} else if (!this.level().isClientSide
						&& (
						this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight())
								|| this.time > 600
				)) {
					if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
						dropItem();
					}

					this.callOnBrokenAfterFall(block, blockpos);
					this.discard();
				}
			}

			this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
		}

		if(blockState.is(AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK) || InfusedPropertiesHelper.hasProperty(getItem(), AlchemancyProperties.LAZY))
		{
			if(getDefaultGravity() < 0)
				setGravity((float) (getDefaultGravity() * 0.95f));
			setDeltaMovement(getDeltaMovement().scale(0.95838f));
		}

		pushEntities();
	}

	public void dropItem() {
		this.spawnAtLocation(this.blockState.getBlock());
	}
	
	@Override
	public boolean isAttackable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!isInvulnerableTo(source) && !level().isClientSide())
		{
			if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
				dropItem();
			}
			this.callOnBrokenAfterFall(this.blockState.getBlock(), new BlockPos(blockPosition().getX(), (int) Math.round(position().y()), blockPosition().getZ()));
			this.discard();
		}

		return false;
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {

		if(!blockState.is(AlchemancyBlocks.PHANTOM_MEMBRANE_BLOCK))
			return super.interactAt(player, vec, hand);

		Direction direction = null;

		if(vec.x() > 0.4) direction =  Direction.EAST;
		else if(vec.x() < -0.4) direction =  Direction.WEST;
		else if(vec.z() > 0.4) direction = Direction.SOUTH;
		else if(vec.z() < -0.4) direction = Direction.NORTH;
		else if(vec.y() > 0.9) direction = Direction.UP;
		else if(vec.y() < 0.1) direction = Direction.DOWN;

		if(direction == null)
			return InteractionResult.PASS;

		ItemStack stack = player.getItemInHand(hand);
		return stack.useOn(new UseOnContext(level(), player, hand, stack, new BlockHitResult(position().add(vec), direction, new BlockPos(blockPosition().getX(), (int) getY(0.5f), blockPosition().getZ())
				.relative(direction), false)));
	}

	private boolean isFree() {

		var direction = getGravity() == 0 ? getDeltaMovement().y() : getGravity();
		return FallingBlock.isFree(this.level().getBlockState(direction < 0 ? blockPosition().above() : blockPosition().below()));
	}

	private void pushEntities()
	{
		if(!level().isClientSide())
			return;

		var delta = getDeltaMovement();

		AABB aabb = getBoundingBox().move(getPosition(0).subtract(position())).inflate(0.1);
		List<Entity> list = level().getEntities(this, aabb);
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (entity.getPistonPushReaction() != PushReaction.IGNORE && !entity.getType().equals(EntityType.FISHING_BOBBER)) {

					var isVertical = !(Math.max(Math.abs(entity.getX() - getX()), Math.abs(entity.getZ() - getZ())) > getBbWidth() * 0.5f);
					var relativePosition = entity.position().subtract(position());

					entity.move(
							MoverType.SHULKER,
							new Vec3(
									isVertical || Math.signum(relativePosition.x()) == Math.signum(delta.x()) ? delta.x()  : 0,
									isVertical ? Math.signum(relativePosition.y()) == Math.signum(delta.y()) ? Math.max(0, getY(Math.max(0, Math.signum(relativePosition.y()))) - entity.getY()) : 0 : 0,
									isVertical || Math.signum(relativePosition.z()) == Math.signum(delta.z()) ? delta.z()  : 0
							)
					);

					if(entity.position().y >= position().y + delta.y)
					{
						entity.setOnGround(true);
						Vec3 entityDelta = entity.getDeltaMovement();
						entity.setDeltaMovement(entityDelta.x, Math.max(0, entityDelta.y), entityDelta.z);
					}
				}
			}
		}
	}

	public static CustomFallingBlock fall(Level level, BlockPos pos, BlockState blockState, ItemStack item) {
		CustomFallingBlock fallingblockentity = new CustomFallingBlock(
				level,
				(double)pos.getX() + 0.5,
				(double)pos.getY(),
				(double)pos.getZ() + 0.5,
				blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : blockState,
				item
		);
		level.setBlock(pos, blockState.getFluidState().createLegacyBlock(), 3);
		level.addFreshEntity(fallingblockentity);
		return fallingblockentity;
	}


	private static final float CONST = 1000;

	@Override
	public void setDeltaMovement(Vec3 deltaMovement) {

		if(!this.getDeltaMovement().scale(0.98).equals(deltaMovement))
			super.setDeltaMovement(deltaMovement);
	}

	@Override
	public boolean canBeCollidedWith() {
		return entityData.get(HAS_COLLISION);
	}

	@Override
	public double getDefaultGravity() {
		return entityData.get(GRAVITY);
	}

	public void setGravity(float v)
	{
		entityData.set(GRAVITY, v);
	}

	public void setHasCollision(boolean v)
	{
		entityData.set(HAS_COLLISION, v);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.putFloat("gravity", (float) getDefaultGravity());
		compound.putBoolean("has_collision", canBeCollidedWith());
		if(!getItem().isEmpty())
			compound.put("Item", this.getItem().save(this.registryAccess()));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains("gravity", CompoundTag.TAG_ANY_NUMERIC))
			setGravity(compound.getFloat("gravity"));
		setHasCollision(compound.getBoolean("has_collision"));

		if (compound.contains("Item", 10)) {
			this.setItem(ItemStack.parse(this.registryAccess(), compound.getCompound("Item")).orElseGet(() -> new ItemStack(this.getDefaultItem())));
		} else {
			this.setItem(new ItemStack(this.getDefaultItem()));
		}
	}

	private ItemLike getDefaultItem() {
		return getBlockState().getBlock();
	}
}
