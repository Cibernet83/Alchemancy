package net.cibernet.alchemancy.properties;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public class DeadProperty extends Property
{
	public static final List<DataComponentType<?>> COMPONENTS_TO_CLEAR = List.of(DataComponents.FOOD, DataComponents.TOOL);

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event) {
		event.clearModifiers();
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(!event.isCanceled())
		{
			event.setCancellationResult(InteractionResult.PASS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		if(!event.isCanceled())
		{
			event.setCancellationResult(InteractionResult.PASS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getPriority() {
		return Priority.HIGHEST;
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		event.setUseItem(TriState.FALSE);
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return COMPONENTS_TO_CLEAR.contains(dataType) ? null : data;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x746A66;
	}
}
