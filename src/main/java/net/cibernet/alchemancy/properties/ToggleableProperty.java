package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ToggleableProperty extends Property implements IDataHolder<Boolean>
{
	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		System.out.println(player.level().isClientSide);
		if(carriedItem.isEmpty() && clickAction == ClickAction.SECONDARY)
		{
			toggle(stackedOnItem, event.getPlayer());
			event.setCanceled(true);
		}
	}

	@Override
	public @org.jetbrains.annotations.Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult)
	{
		toggle(root.getItem(), user);
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if(!event.isCanceled())
			toggle(event.getItemStack(), event.getEntity());
	}

	@Override
	public int getPriority() {
		return Priority.LOWEST;
	}

	public void toggle(ItemStack stack, @Nullable Entity entity)
	{
		boolean active = getData(stack);
		setData(stack, !active);
		if(entity != null)
			entity.playSound(SoundEvents.LEVER_CLICK, 0.3F, active ? 0.5F : 0.6F);
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
