package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlameWakerProperty extends Property
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY)
		{
			Level level = user.level();
			BlockPos pos = user.blockPosition();
			if (BaseFireBlock.canBePlacedAt(level, pos, Direction.UP) || level.getBlockState(pos).canBeReplaced())
			{
				BlockState blockstate1 = BaseFireBlock.getState(level, pos);
				level.setBlock(pos, blockstate1, 11);
			}
		}

	}

	@Override
	public int getColor(ItemStack stack) {
		return 0;
	}
}
