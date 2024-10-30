package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class FlattenedProperty extends IncreaseInfuseSlotsProperty
{
	public FlattenedProperty() {
		super(1);
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.isCanceled() || InfusedPropertiesHelper.hasProperty(event.getItemStack(), AlchemancyProperties.DEAD))
			return;

		BlockPos pos = event.getPos();

		if(!event.getLevel().getBlockState(pos).canBeReplaced(new BlockPlaceContext(event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec())))
			pos = pos.relative(event.getFace() == null ? Direction.UP : event.getFace());

		BlockState blockState = AlchemancyBlocks.FLATTENED_ITEM.get().getStateForPlacement(new BlockPlaceContext(event.getEntity(), event.getHand(), event.getItemStack(), event.getHitVec()));
		if(blockState.canSurvive(event.getLevel(), pos))
		{
			event.getLevel().setBlock(pos, blockState, 3);
			ItemStackHolderBlockEntity blockEntity = new ItemStackHolderBlockEntity(pos, blockState);
			blockEntity.setItem(event.getItemStack().split(1));
			event.getLevel().setBlockEntity(blockEntity);

			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFFFFFF;
	}
}
