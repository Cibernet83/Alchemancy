package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.registries.AlchemancyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GlowRingProperty extends Property
{	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		Level level = user.level();
		BlockPos pos = user.blockPosition();
		if(level.getBrightness(LightLayer.BLOCK, pos) <= 7 && level.getBlockState(pos).canBeReplaced())
		{
			BlockState state = AlchemancyBlocks.GLOWING_ORB.get().getStateForPlacement(new BlockPlaceContext(level, user instanceof Player player ? player : null, InteractionHand.MAIN_HAND, ItemStack.EMPTY,
					new BlockHitResult(pos.getCenter(), Direction.UP, pos, false)));
			level.setBlock(pos, state == null ? AlchemancyBlocks.GLOWING_ORB.get().defaultBlockState() : state, 11);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFFFFBA;
	}
}
