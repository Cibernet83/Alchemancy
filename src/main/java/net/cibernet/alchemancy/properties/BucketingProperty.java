package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class BucketingProperty extends Property implements IDataHolder<Fluid>
{

	@Override
	public int getColor(ItemStack stack) {
		return 0x26546D;
	}

	@Override
	public Component getName(ItemStack stack)
	{
		Component name = super.getName(stack);
		Fluid storedFluid = getData(stack);

		if(!storedFluid.equals(getDefaultData()))
			return Component.translatable("property.detail", name, storedFluid.defaultFluidState().createLegacyBlock().getBlock().getName()).withColor(getColor(stack));
		return name;
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled() && handleInteraction(event))
		{
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(!event.isCanceled() && handleInteraction(event))
		{
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	private boolean handleInteraction(PlayerInteractEvent event)
	{
		Level level = event.getLevel();
		ItemStack stack = event.getItemStack();
		Fluid storedFluid = getData(stack);
		BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, event.getEntity(), storedFluid.equals(Fluids.EMPTY) ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
		Player user = event.getEntity();
		BlockPos hitPos = hitResult.getBlockPos();
		BlockState hitState = level.getBlockState(hitPos);
		Direction hitDirection = hitResult.getDirection();

		if(hitResult.getType() == HitResult.Type.BLOCK)
		{
			if(storedFluid.equals(getDefaultData()))
			{

				if(hitState.getBlock() instanceof BucketPickup bucketPickup && bucketPickup.pickupBlock(user, level, hitPos, hitState).getItem() instanceof BucketItem bucketItem)
				{
					bucketPickup.getPickupSound(hitState).ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));
					level.gameEvent(user, GameEvent.FLUID_PICKUP, hitPos);

					setData(stack, bucketItem.content);
					return true;
				}
			}
			else if(placeLiquid(level, hitPos, storedFluid, user, hitDirection))
			{
				setData(stack, getDefaultData());
				return true;
			}
		}

		return false;
	}

	public boolean isEmpty(ItemStack stack)
	{
		return getData(stack).equals(getDefaultData());
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		if(placeLiquid(itemEntity.level(), itemEntity.blockPosition(), getData(stack), null, null))
			setData(stack, getDefaultData());
	}

	public boolean placeLiquid(Level level, BlockPos hitPos, ItemStack sourceStack, @Nullable Player user, @Nullable Direction hitDirection)
	{
		if(placeLiquid(level, hitPos, getData(sourceStack), user, hitDirection))
		{
			setData(sourceStack, getDefaultData());
			return true;
		}
		return false;
	}

	protected boolean placeLiquid(Level level, BlockPos hitPos, Fluid storedFluid, @Nullable Player user, @Nullable Direction hitDirection)
	{
		if(storedFluid.equals(getDefaultData()))
			return false;

		BlockState hitState = level.getBlockState(hitPos);
		if(hitState.getBlock() instanceof LiquidBlockContainer liquidBlockContainer && liquidBlockContainer.canPlaceLiquid(user, level, hitPos, hitState, storedFluid))
		{
			liquidBlockContainer.placeLiquid(level, hitPos, hitState, storedFluid.defaultFluidState());
			playEmptySound(user, level, hitPos, storedFluid);

			return true;
		}
		else if(storedFluid.getFluidType().isVaporizedOnPlacement(level, hitPos, FluidStack.EMPTY))
		{
			storedFluid.getFluidType().onVaporize(user, level, hitPos, FluidStack.EMPTY);

			return true;
		}
		else if (level.dimensionType().ultraWarm() && storedFluid.is(FluidTags.WATER)) {
			int l = hitPos.getX();
			int i = hitPos.getY();
			int j = hitPos.getZ();
			level.playSound(
					user,
					hitPos,
					SoundEvents.FIRE_EXTINGUISH,
					SoundSource.BLOCKS,
					0.5F,
					2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
			);

			for (int k = 0; k < 8; k++) {
				level.addParticle(
						ParticleTypes.LARGE_SMOKE, (double)l + Math.random(), (double)i + Math.random(), (double)j + Math.random(), 0.0, 0.0, 0.0
				);
			}

			return true;
		}
		else
		{
			if (hitState.canBeReplaced(storedFluid))
			{
				if(!level.isClientSide() && !hitState.liquid())
					level.destroyBlock(hitPos, true);
			}
			else if(hitDirection != null)
				{
					hitPos = hitPos.relative(hitDirection);
					hitState = level.getBlockState(hitPos);
				}

			if (((hitState.isAir() || hitState.canBeReplaced(storedFluid)) && level.setBlock(hitPos, storedFluid.defaultFluidState().createLegacyBlock(), 11)) || hitState.getFluidState().isSource()) {
				this.playEmptySound(user, level, hitPos, storedFluid);
				return true;
			}
		}
		return false;
	}

	protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos, Fluid fluid) {
		SoundEvent soundevent = fluid.getFluidType().getSound(player, level, pos, net.neoforged.neoforge.common.SoundActions.BUCKET_EMPTY);
		if(soundevent == null) soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		level.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
	}

	@Override
	public Fluid readData(CompoundTag tag)
	{
		if(tag.contains("fluid", Tag.TAG_STRING))
		{
			Optional<Holder.Reference<Fluid>> fluid = CommonUtils.registryAccessStatic().lookupOrThrow(Registries.FLUID).get(ResourceKey.create(Registries.FLUID, ResourceLocation.parse(tag.getString("fluid"))));
			if(fluid.isPresent())
				return fluid.get().value();
		}

		return getDefaultData();
	}

	@Override
	public CompoundTag writeData(Fluid data) {
		return new CompoundTag(){{
			if(!data.equals(Fluids.EMPTY))
				putString("fluid", BuiltInRegistries.FLUID.getKey(data).toString());
		}};
	}

	@Override
	public Fluid getDefaultData() {
		return Fluids.EMPTY;
	}
}
