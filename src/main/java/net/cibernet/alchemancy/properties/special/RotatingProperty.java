package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jetbrains.annotations.Nullable;

public class RotatingProperty extends Property {

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult)
	{
		if(rotateBlock(blockSource.level(), blockSource.pos().relative(direction), direction.getOpposite()))
			return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
		else return currentResult;
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event) {

		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		Direction face = event.getFace();

		if(face == null) return;

		if(rotateBlock(level, pos, face))
		{
			event.setCancellationResult(ItemInteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	private static boolean rotateBlock(Level level, BlockPos pos, Direction face) {

		BlockState state = level.getBlockState(pos);
		BlockState newState = null;
		if(state.hasProperty(BlockStateProperties.FACING))
			newState = state.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING).getClockWise(face.getAxis()));
		else if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING))
			newState = state.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
		else if(state.hasProperty(BlockStateProperties.FACING_HOPPER))
			newState = state.setValue(BlockStateProperties.FACING_HOPPER, state.getValue(BlockStateProperties.FACING_HOPPER).getClockWise());
		else if(state.hasProperty(BlockStateProperties.AXIS))
			newState = state.setValue(BlockStateProperties.AXIS, rotateAxis(state.getValue(BlockStateProperties.AXIS), face.getAxis()));
		else if(state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
			newState = state.setValue(BlockStateProperties.AXIS, rotateAxis(state.getValue(BlockStateProperties.AXIS), Direction.Axis.Y));

		if(newState != null && newState.canSurvive(level, pos))
		{
			level.setBlock(pos, newState, Block.UPDATE_ALL);
			return true;
		} else return false;
	}

	private static Direction.Axis rotateAxis(Direction.Axis axis, Direction.Axis from) {

		if(from == Direction.Axis.X)
		{
			return switch (axis){
				case X -> Direction.Axis.X;
				case Y -> Direction.Axis.Z;
				case Z -> Direction.Axis.Y;
			};
		}
		else if(from == Direction.Axis.Y)
		{
			return switch (axis){
				case X -> Direction.Axis.Z;
				case Y -> Direction.Axis.Y;
				case Z -> Direction.Axis.X;
			};
		}
		else
		{
			return switch (axis){
				case X -> Direction.Axis.Y;
				case Y -> Direction.Axis.X;
				case Z -> Direction.Axis.Z;
			};
		}
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

		rotateEntity(event.getTarget());
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
		rotateEntity(target);
	}

	private void rotateEntity(Entity target) {
		float diff = target.getYHeadRot() - 45;
		target.setYBodyRot(diff);
		target.setYHeadRot(diff);
		target.setOnGround(false);
		target.hurtMarked = true;

		if (target instanceof LivingEntity living)
			living.setNoActionTime(60);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFFF787;
	}
}
