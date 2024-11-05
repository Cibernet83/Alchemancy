package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemStackHolderBlockEntity extends BaseContainerBlockEntity
{
	NonNullList<ItemStack> slot = NonNullList.withSize(1, ItemStack.EMPTY);

	public ItemStackHolderBlockEntity(BlockPos pos, BlockState blockState)
	{
		super(AlchemancyBlockEntities.ITEMSTACK_HOLDER.get(), pos, blockState);
	}

	public ItemStackHolderBlockEntity(BlockEntityType<? extends ItemStackHolderBlockEntity> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return null;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return slot;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		slot = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	public ItemStack getItem()
	{
		return super.getItem(0);
	}

	public ItemStack removeItem(int amount)
	{
		return removeItem(0, amount);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		notifyInventoryUpdate();
	}

	@Override
	public ItemStack removeItem(int slot, int amount)
	{
		ItemStack result = super.removeItem(slot, amount);
		notifyInventoryUpdate();
		return result;
	}

	@Override
	public void clearContent() {
		super.clearContent();
		notifyInventoryUpdate();
	}

	public void setItem(ItemStack stack)
	{
		setItem(0, stack);
	}

	public void notifyInventoryUpdate()
	{
		if(level != null)
			level.markAndNotifyBlock(getBlockPos(), level.getChunkAt(getBlockPos()), getBlockState(), getBlockState(), 2, 1);
	}

	public static void dropItem(Level pLevel, BlockPos pPos, ItemStack itemstack)
	{
		if (!pLevel.isClientSide)
		{
			if (!itemstack.isEmpty())
			{
				ItemStack itemstack1 = itemstack.copy();
				ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() +.5, pPos.getY() + 1, pPos.getZ() + .5, itemstack1);
				itementity.setDeltaMovement(0, .15, 0);
				itementity.setDefaultPickUpDelay();
				itementity.getPersistentData().putBoolean(Alchemancy.MODID + ":from_pedestal", true);

				pLevel.addFreshEntity(itementity);
			}

		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		ContainerHelper.saveAllItems(tag, this.slot, registries);
	}


	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);

		slot.clear();
		ContainerHelper.loadAllItems(tag, this.slot, registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag, registries);

		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		// Will get tag from #getUpdateTag
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
