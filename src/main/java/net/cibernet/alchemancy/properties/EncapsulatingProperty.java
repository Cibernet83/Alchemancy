package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.Optional;

public class EncapsulatingProperty extends Property implements IDataHolder<EncapsulatingProperty.BlockData>
{

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockData data = getData(event.getItemStack());

		if(data.blockState.isAir())
		{
			BlockState state = level.getBlockState(pos);
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if(!state.isAir() && state.getDestroySpeed(level, pos) >= 0)
			{
				if(blockEntity != null)
					level.removeBlockEntity(pos);
				level.removeBlock(pos, false);
				setData(event.getItemStack(), state, blockEntity);

				level.playSound(null, pos, data.blockState.getSoundType(level, pos, event.getEntity()).getBreakSound(), SoundSource.BLOCKS, 1.0f, 0.5f);

				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
		else
		{
			if (event.getFace() != null && !level.getBlockState(pos).canBeReplaced())
				pos = pos.relative(event.getFace());

			if(attemptPlaceBlock(level, pos, data, event.getItemStack(), event.getEntity()))
			{
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		attemptPlaceBlock(itemEntity.level(), itemEntity.blockPosition(), getData(stack), stack, null);
	}

	public boolean attemptPlaceBlock(Level level, BlockPos pos, BlockData data, ItemStack source, @Nullable Entity user)
	{
		if (!level.getBlockState(pos).canBeReplaced() || !data.blockState.canSurvive(level, pos))
			return false;

		level.setBlockAndUpdate(pos, data.blockState);

		BlockEntity blockEntity = createBlockEntity(pos, data);
		if(blockEntity != null)
			level.setBlockEntity(blockEntity);

		level.playSound(null, pos, data.blockState.getSoundType(level, pos, user).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.5f);

		setData(source, getDefaultData());
		return true;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x976997;
	}

	@Override
	public Component getName(ItemStack stack)
	{
		Component name = super.getName(stack);
		BlockState storedBlock = getData(stack).blockState;

		if(!storedBlock.isAir())
			return Component.translatable("property.detail", name, storedBlock.getBlock().getName()).withColor(getColor(stack));
		return name;
	}

	@Nullable
	public BlockEntity createBlockEntity(BlockPos pos, ItemStack dataItem)
	{
		return createBlockEntity(pos, getData(dataItem));
	}

	@Nullable
	public BlockEntity createBlockEntity(BlockPos pos, BlockData data)
	{
		return createBlockEntity(data.blockState, pos, data.blockEntityData);
	}

	@Nullable
	public BlockEntity createBlockEntity(BlockState state, BlockPos pos, CompoundTag data)
	{
		if(state.getBlock() instanceof EntityBlock entityBlock) {
			BlockEntity result = entityBlock.newBlockEntity(pos, state);
			if(result != null)
			{
				result.loadCustomOnly(data, CommonUtils.registryAccessStatic());
				return result;
			}
		}

		return null;
	}

	public void setData(ItemStack stack, BlockState state, @Nullable BlockEntity blockEntity)
	{
		IDataHolder.super.setData(stack, new BlockData(state, blockEntity == null ? new CompoundTag() : blockEntity.saveCustomOnly(CommonUtils.registryAccessStatic())));
	}

	@Override
	public BlockData readData(CompoundTag tag)
	{

		if(tag.contains("block_id", Tag.TAG_STRING))
		{
			RegistryAccess registryAccess = CommonUtils.registryAccessStatic();


			Optional<Holder.Reference<Block>> block = registryAccess.lookupOrThrow(Registries.BLOCK).get(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse(tag.getString("block_id"))));

			if(block.isPresent())
			{
				BlockState state = block.get().value().defaultBlockState();

				if(tag.contains("block_state", Tag.TAG_COMPOUND))
				{
					StateDefinition<Block, BlockState> stateDefinition = state.getBlock().getStateDefinition();
					CompoundTag blockStateTag = tag.getCompound("block_state");
					for (String statePropertyName : blockStateTag.getAllKeys())
					{
						net.minecraft.world.level.block.state.properties.Property<?> stateProperty = stateDefinition.getProperty(statePropertyName);

						if(stateProperty != null)
							state = updateState(state, stateProperty, blockStateTag.getString(statePropertyName));
					}
				}

				return new BlockData(state, tag.getCompound("block_entity"));
			}
		}
		return getDefaultData();
	}

	@Override
	public CompoundTag writeData(BlockData data)
	{
		return new CompoundTag(){{
			RegistryAccess registryAccess = CommonUtils.registryAccessStatic();
			if(!data.blockEntityData.isEmpty())
				put("block_entity", data.blockEntityData);
			if(!data.blockState.isAir())
			{
				final BlockState state = data.blockState;

				if(!state.getValues().isEmpty())
				{
					CompoundTag statesTag = new CompoundTag();
					for (net.minecraft.world.level.block.state.properties.Property<?> stateProperty : state.getValues().keySet()) {
						statesTag.putString(stateProperty.getName(), getStateValueName(stateProperty, state));
					}
					put("block_state", statesTag);
				}

				putString("block_id", state.getBlockHolder().getKey().location().toString());
			}
		}};
	}

	private static <T extends Comparable<T>> BlockState updateState(BlockState state, net.minecraft.world.level.block.state.properties.Property<T> property, String propertyName) {
		return property.getValue(propertyName).map(p_330629_ -> state.setValue(property, p_330629_)).orElse(state);
	}

	private <T extends Comparable<T>> String getStateValueName(net.minecraft.world.level.block.state.properties.Property<T> property, BlockState state)
	{
		return property.getName(state.getValue(property));
	}

	private static final BlockData DEFAULT = new BlockData(Blocks.AIR.defaultBlockState(), new CompoundTag());

	@Override
	public BlockData getDefaultData() {
		return DEFAULT;
	}

	public record BlockData(BlockState blockState, CompoundTag blockEntityData) {}
}
