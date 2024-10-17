package net.cibernet.alchemancy.entity;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.mixin.accessors.FallingBlockEntityAccessor;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CustomFallingBlock extends FallingBlockEntity
{
	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> HAS_COLLISION = SynchedEntityData.defineId(CustomFallingBlock.class, EntityDataSerializers.BOOLEAN);

	public CustomFallingBlock(EntityType<? extends FallingBlockEntity> entityType, Level level) {
		super(entityType, level);
	}

	private CustomFallingBlock(Level level, double x, double y, double z, BlockState state) {
		this(AlchemancyEntities.FALLING_BLOCK.get(), level);
		((FallingBlockEntityAccessor)this).setBlockState(state);
		this.blocksBuilding = true;
		this.setPos(x, y, z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(this.blockPosition());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(GRAVITY, 0.04f);
		builder.define(HAS_COLLISION, false);
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
		super.tick();

		pushEntities(getDeltaMovement());
	}

	private void pushEntities(Vec3 delta)
	{
		if (delta.length() > 0.0F)
		{

			List<Entity> entities = this.level().getEntities(this, getBoundingBox().expandTowards(delta.scale(2)),
					EntitySelector.NO_SPECTATORS.and(p_149771_ -> !p_149771_.isPassengerOfSameVehicle(this)));

			Alchemancy.LOGGER.info("step 1: {}", delta);
			for (Entity entity : entities)
			{
				if (!entity.noPhysics && !(entity instanceof CustomFallingBlock))
				{

					//Vec3 centerPos = position().add(0, getBbHeight() * 0.5, 0);
					//Vec3 faceNormal = delta.normalize().multiply(getBbWidth() * 0.5, getBbHeight() * 0.5, getBbWidth() * 0.5);

					//entity.setDeltaMovement(entity.getDeltaMovement())); //TODO find a way to make bouncy do this
					entity.move(MoverType.SHULKER, getDeltaMovement());
					if(entity.position().y >= position().y + getDeltaMovement().y)
					{
						entity.setOnGround(true);
						Vec3 entityDelta = entity.getDeltaMovement();
						entity.setDeltaMovement(entityDelta.x, Math.max(0, entityDelta.y), entityDelta.z);
						entity.hasImpulse = true;
					}
				}
			}
		}
	}

	public static CustomFallingBlock fall(Level level, BlockPos pos, BlockState blockState) {
		CustomFallingBlock fallingblockentity = new CustomFallingBlock(
				level,
				(double)pos.getX() + 0.5,
				(double)pos.getY(),
				(double)pos.getZ() + 0.5,
				blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)) : blockState
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
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains("gravity", CompoundTag.TAG_ANY_NUMERIC))
			setGravity(compound.getFloat("gravity"));
		setHasCollision(compound.getBoolean("has_collision"));
	}
}
