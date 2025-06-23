package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class PhotosyntheticProperty extends Property
{

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(canRepair(stack) && user.tickCount % 600 == 0 && canPhotosynthesize(level, user.blockPosition()))
			repairItem(stack, 1);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		Level level = root.getLevel();
		ItemStack stack = root.getItem();

		if(canRepair(stack) && root.getTickCount() % 300 == 0 && canPhotosynthesize(level, root.getBlockPos()))
		{
			repairItem(stack, 1);
		}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		BlockPos pPos = root.getBlockPos();
		Level level = root.getLevel();

		if(root.getItem().isDamaged() && canPhotosynthesize(level, pPos))
		{
			playRootedParticles(root, random, ParticleTypes.WAX_ON);
		}
	}

	public static boolean canPhotosynthesize(Level level, BlockPos pos)
	{
		return level.canSeeSky(pos) && level.isDay();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x42BC16;
	}
}
