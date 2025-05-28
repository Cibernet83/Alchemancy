package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FlameWakerProperty extends Property {
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if (user.level().isClientSide() || !(slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY)) return;

		Level level = user.level();
		BlockPos pos = user.blockPosition();
		BlockState state = level.getBlockState(pos);

		if(user.getKnownMovement().length() <= 0.005f && state.is(BlockTags.FIRE) && state.hasProperty(FireBlock.AGE))
		{
			level.setBlock(pos, state.setValue(FireBlock.AGE, Math.min(state.getValue(FireBlock.AGE), 2)), 11);
		}
		if (user.isSprinting() && (BaseFireBlock.canBePlacedAt(level, pos, Direction.UP) || level.getBlockState(pos).canBeReplaced())) {
			BlockState fireBlock = BaseFireBlock.getState(level, pos);
			if(fireBlock.hasProperty(FireBlock.AGE))
				fireBlock = fireBlock.setValue(FireBlock.AGE, 8);
			level.setBlock(pos, fireBlock, 11);
		}

		stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 1), user, slot);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF7022;
	}
}
