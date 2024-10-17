package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.entity.CustomFallingBlock;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class LooseProperty extends Property
{
	@Override
	public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
		return EquipmentSlot.MAINHAND;
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.isCanceled() && stack.getItem() instanceof BlockItem blockItem)
		{
			Level level = event.getLevel();
			BlockPos pos = event.getPos().relative(event.getFace());

			if(FallingBlock.isFree(level.getBlockState(pos.below())) &&
					blockItem.useOn(new UseOnContext(event.getLevel(), event.getEntity(), event.getHand(), stack, event.getHitVec())).consumesAction())
			{
				CustomFallingBlock fallingBlockEntity = CustomFallingBlock.fall(level, pos, level.getBlockState(pos));

				if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.STURDY))
					fallingBlockEntity.setHasCollision(true);

				if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.LEVITATING))
					fallingBlockEntity.setGravity(-0.01f);
				else if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.ANTIGRAV))
					fallingBlockEntity.setGravity(0);

				event.setCancellationResult(InteractionResult.SUCCESS);
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
