package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.common.ticket.SimpleTicket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RootedItemBlockEntity extends ItemStackHolderBlockEntity
{
	private int tickCount = 0;

	@Nullable
	private SimpleTicket<Vec3> farmlandWaterManager;

	public RootedItemBlockEntity(BlockPos pPos, BlockState pBlockState)
	{
		super(AlchemancyBlockEntities.ROOTED_ITEM.get(), pPos, pBlockState);
	}

	@Override
	public void invalidateCapabilities()
	{
		if(farmlandWaterManager != null && farmlandWaterManager.isValid())
			farmlandWaterManager.invalidate();
		super.invalidateCapabilities();
	}


	public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RootedItemBlockEntity root)
	{
		List<LivingEntity> entitiesInBounds = pLevel.getEntitiesOfClass(LivingEntity.class, root.getBlockState().getShape(pLevel, pPos).bounds().move(pPos));
		InfusedPropertiesHelper.forEachProperty(root.getItem(), propertyHolder -> propertyHolder.value().onRootedTick(root, entitiesInBounds));

		if(root.getItem().isEmpty())
			pLevel.destroyBlock(pPos, true);

		root.tickCount++;
	}

	public void setFarmlandWaterManager(SimpleTicket<Vec3> ticket)
	{
		if(farmlandWaterManager != null)
			this.farmlandWaterManager = ticket;
	}

	public int getTickCount()
	{
		return tickCount;
	}
}
