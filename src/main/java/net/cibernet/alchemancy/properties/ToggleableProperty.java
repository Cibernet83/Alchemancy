package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToggleableProperty extends Property implements IDataHolder<Boolean>
{
	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled)
	{
		if(carriedItem.isEmpty() && clickAction == ClickAction.SECONDARY)
		{
			toggle(stackedOnItem, player);
			isCancelled.set(true);
		}
	}

	@Override
	public @org.jetbrains.annotations.Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult)
	{
		toggle(root.getItem(), user);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getEntity().isShiftKeyDown())
		{
			toggle(event.getItemStack(), event.getEntity());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickItemPost(PlayerInteractEvent.RightClickItem event) {
		if(!event.isCanceled() && !event.getEntity().isShiftKeyDown())
			toggle(event.getItemStack(), event.getEntity());
	}

	public void toggle(ItemStack stack, @Nullable Entity entity)
	{
		boolean active = getData(stack);
		setData(stack, !active);
		if(entity != null)
			entity.playSound(AlchemancySoundEvents.TOGGLEABLE.value(), 0.3F, active ? 0.5F : 0.6F);
	}

	@Override
	public int getColor(ItemStack stack) {
		return getData(stack) ? 0xE50000 : 0x410500;
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("active");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag(){{putBoolean("active", data);}};
	}

	@Override
	public Boolean getDefaultData() {
		return true;
	}
}
