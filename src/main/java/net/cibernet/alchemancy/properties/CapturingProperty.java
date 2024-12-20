package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CapturingProperty extends Property implements IDataHolder<CapturingProperty.EntityData<?>>
{
	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event)
	{
		if(rayTraceResult.getType() == HitResult.Type.ENTITY && rayTraceResult instanceof EntityHitResult entityHitResult && captureMob(entityHitResult.getEntity(), stack))
		{
			projectile.spawnAtLocation(stack);
			projectile.discard();
			event.setCanceled(true);
		}
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		releaseMob(itemEntity.level(), stack, itemEntity.position(), itemEntity.getDeltaMovement().scale(-1));
	}

	@Override
	public void onActivation(Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		Vec3 lookVec = new Vec3(0, 1, 0);
		if(source == null)
			source = target;
		else lookVec = source.getLookAngle();
		releaseMob(source.level(), stack, source.position().add(lookVec), lookVec);
	}

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult)
	{
		if(releaseMob(blockSource.level(), stack, blockSource.pos().relative(direction).getCenter(), new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ())))
		{
			InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
			return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
		}
		else
		{
			ServerLevel serverlevel = blockSource.level();
			BlockPos blockpos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
			List<Entity> list = serverlevel.getEntitiesOfClass(Entity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);

			if(!list.isEmpty() && captureMob(list.getFirst(), stack))
			{
				InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
				return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
			}
		}
		return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		Vec3 lookVec = event.getEntity().getLookAngle();
		if(!event.isCanceled())
		{
			if(getData(event.getItemStack()).equals(getDefaultData())) {
				if (releaseMob(event.getLevel(), event.getItemStack(), event.getEntity().position().add(lookVec), lookVec)) {
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
				}
			}
			else
			{
				EntityHitResult hitResult = getPlayerPOVHitResult(event.getLevel(), event.getEntity());
				if(hitResult != null && captureMob(hitResult.getEntity(), event.getItemStack()))
				{
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
				}
			}
		}
	}


	public static EntityHitResult getPlayerPOVHitResult(Level level, Player player) {
		Vec3 vec3 = player.getEyePosition();
		Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.entityInteractionRange()));

		return ProjectileUtil.getEntityHitResult(level, player, player.position(), vec31, player.getBoundingBox(), entity -> true);
	}


	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Vec3i faceNormal = event.getFace().getNormal();
		if(releaseMob(event.getLevel(), event.getItemStack(), event.getPos().relative(event.getFace()).getCenter(), new Vec3(faceNormal.getX(), faceNormal.getY(), faceNormal.getZ())))
		{
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		if(captureMob(event.getTarget(), event.getItemStack()))
		{
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	public boolean releaseMob(Level level, ItemStack stack, Vec3 position, @Nullable Vec3 direction)
	{
		EntityData<?> data = getData(stack);
		if(level.isClientSide() || data.equals(getDefaultData()))
			return false;

		Entity entity = getEntityFromData(data, level);
		if(entity == null)
			return false;

		entity.setPos(position);
		if(direction != null)
			setMobDirection(entity, direction);

		if(((ServerLevel)level).getEntity(entity.getUUID()) != null)
			entity.setUUID(Mth.createInsecureUUID(entity.getRandom()));
		level.addFreshEntity(entity);


		setData(stack, getDefaultData());
		return true;
	}

	public boolean captureMob(@Nonnull Entity target, ItemStack stack)
	{
		if(!getData(stack).equals(getDefaultData()) || target.getType().is(AlchemancyTags.EntityTypes.CANNOT_CAPTURE))
			return false;

		setData(stack, getDataFromEntity(target));
		target.discard();
		return true;
	}

	public static void setMobDirection(@Nonnull Entity target, Vec3 direction)
	{
		target.setDeltaMovement(direction.normalize().scale(target.getDeltaMovement().length()));
		target.hurtMarked = true;
		target.hasImpulse = true;
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x256B2B;
	}

	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		EntityData<?> entityData = getData(stack);

		if(!entityData.equals(getDefaultData()))
			return Component.translatable("property.detail", name, entityData.name).withColor(getColor(stack));
		return name;
	}

	public EntityData<?> getDataFromEntity(Entity entity)
	{
		return new EntityData<>(entity.getType(), new CompoundTag(){{entity.save(this);}}, entity.getName());
	}

	@Nullable
	public <T extends Entity> T getEntityFromData(EntityData<T> data, Level level)
	{
		if(data.entityType != null)
		{
			T entity = data.entityType.create(level);
			if(entity != null)
			{
				entity.load(data.data);
				return entity;
			}
		}
		return null;
	}

	@Override
	public EntityData<?> readData(CompoundTag tag)
	{
		if(tag.contains("entity_id", Tag.TAG_STRING))
		{
			Optional<Holder.Reference<EntityType<?>>> entityType = CommonUtils.registryAccessStatic().lookupOrThrow(Registries.ENTITY_TYPE)
					.get(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(tag.getString("entity_id"))));
			if(entityType.isPresent())
				return new EntityData<>(entityType.get().value(), tag.getCompound("data"), tag.contains("name", Tag.TAG_STRING) ?
						Component.Serializer.fromJson(tag.getString("name"), CommonUtils.registryAccessStatic()) : Component.empty());
		}

		return getDefaultData();
	}

	@Override
	public CompoundTag writeData(EntityData<?> data)
	{
		return new CompoundTag(){{
			if(data.entityType != null)
			{
				putString("entity_id", BuiltInRegistries.ENTITY_TYPE.getKey(data.entityType).toString());

				if(!data.data.isEmpty())
					put("data", data.data);
				putString("name", Component.Serializer.toJson(data.name, CommonUtils.registryAccessStatic()));
			}
		}};
	}

	private static final EntityData<?> DEFAULT = new EntityData<>(null, new CompoundTag(), Component.empty());

	@Override
	public EntityData<?> getDefaultData() {
		return DEFAULT;
	}

	public record EntityData<T extends Entity>(@Nullable EntityType<T> entityType, CompoundTag data, Component name) {}
}
