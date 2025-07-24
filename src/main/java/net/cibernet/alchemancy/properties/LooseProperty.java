package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.entity.CustomFallingBlock;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jetbrains.annotations.Nullable;

public class LooseProperty extends Property
{
	@Override
	public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
		return EquipmentSlot.MAINHAND;
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.isCanceled() && stack.getItem() instanceof BlockItem blockItem)
		{
			Level level = event.getLevel();
			BlockPos pos = event.getPos().relative(event.getFace());

			boolean levitating = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LEVITATING);

			if(FallingBlock.isFree(level.getBlockState(levitating ? pos.above() : pos.below())) &&
					blockItem.useOn(event.getUseOnContext()).consumesAction())
			{
				CustomFallingBlock fallingBlockEntity = CustomFallingBlock.fall(level, pos, level.getBlockState(pos), stack);

				if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.STURDY))
					fallingBlockEntity.setHasCollision(true);

				if(levitating)
					fallingBlockEntity.setGravity(-0.0075f);
				else if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ANTIGRAV))
					fallingBlockEntity.setGravity(0);

				event.setCancellationResult(ItemInteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE3DBB0;
	}

	@Override
	public int getPriority() {
		return Priority.LOWER;
	}
}
