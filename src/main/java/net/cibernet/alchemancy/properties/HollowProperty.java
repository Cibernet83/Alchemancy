package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HollowProperty extends Property implements IDataHolder<ItemStack>
{
	@Override
	public int getColor(ItemStack stack) {
		return 0x705027;
	}

	@Override
	public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data)
	{
		return dataType == DataComponents.MAX_STACK_SIZE ? 1 : data;
	}

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult)
	{
		ItemStack storedStack = getData(stack);

		if(!stack.isEmpty())
		{
			DefaultDispenseItemBehavior.spawnItem(blockSource.level(), storedStack, 6, direction, DispenserBlock.getDispensePosition(blockSource));
			setData(stack, getDefaultData());
			InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
			return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
		}

		return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
	}

	@Override
	public void onPickUpAnyItem(Player user, ItemStack stack, EquipmentSlot slot, ItemEntity itemToPickUp, boolean canPickUp, ItemEntityPickupEvent.Pre event)
	{
		if(!canPickUp)
			return;

		ItemStack storedStack = getData(stack);
		ItemStack stackToPickUp = event.getItemEntity().getItem();

		int toPickUp = 0;

		if(storedStack.isEmpty())
		{
			storedStack = stackToPickUp.copy();
			toPickUp = stackToPickUp.getCount();
			stackToPickUp.setCount(0);
		}
		else if (ItemStack.isSameItemSameComponents(storedStack, stackToPickUp))
		{
			int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
			toPickUp = Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + stackToPickUp.getCount());
			storedStack.setCount(toPickUp);
			stackToPickUp.shrink(mergeLimit);
		}

		if(toPickUp > 0)
		{
			playInsertSound(user);
			user.take(itemToPickUp, toPickUp);
			setData(stack, storedStack);
		}
	}

	@Override
	public void onStackedOverItem(ItemStack hollowItem, ItemStack carriedItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction != ClickAction.SECONDARY)
			return;

		ItemStack storedStack = getData(hollowItem);
		if(!storedStack.isEmpty())
		{
			if(carriedItem.isEmpty())
			{
				event.getSlot().set(storedStack);
				setData(hollowItem, getDefaultData());
				event.setCanceled(true);
				return;
			}
			else if (ItemStack.isSameItemSameComponents(storedStack, carriedItem))
			{
				int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
				storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + carriedItem.getCount()));
				carriedItem.shrink(mergeLimit);

				setData(hollowItem, storedStack);
				event.getSlot().set(carriedItem);
				event.setCanceled(true);
			}
		}
		if(!carriedItem.isEmpty() && storeItem(player, hollowItem, carriedItem))
		{
			event.getSlot().set(carriedItem);
			event.setCanceled(true);
		}
	}



	@Override
	public void onStackedOverMe(ItemStack carriedItem, ItemStack stackedOnItem, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction != ClickAction.SECONDARY)
			return;

		ItemStack storedStack = getData(stackedOnItem);
		if(!storedStack.isEmpty())
		{
			if(carriedItem.isEmpty())
			{
				event.getCarriedSlotAccess().set(storedStack);
				setData(stackedOnItem, getDefaultData());
				event.setCanceled(true);
				return;
			}
			else if(ItemStack.isSameItemSameComponents(storedStack, carriedItem))
			{
				int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
				storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + carriedItem.getCount()));
				carriedItem.shrink(mergeLimit);

				setData(stackedOnItem, storedStack);
				event.getCarriedSlotAccess().set(carriedItem);
				event.setCanceled(true);
			}
		}

		if(!carriedItem.isEmpty() && storeItem(player, stackedOnItem, carriedItem))
		{
			event.getCarriedSlotAccess().set(carriedItem);
			event.setCanceled(true);
		}
	}

	public boolean storeItem(@Nullable Entity player, ItemStack hollowItem, ItemStack itemToPickUp)
	{
		ItemStack storedStack = getData(hollowItem);
		if (canStore(hollowItem, itemToPickUp))
		{
			if(storedStack.isEmpty())
			{
				storedStack = itemToPickUp.copy();
				itemToPickUp.setCount(0);
			} else
			{
				int mergeLimit = storedStack.getMaxStackSize() - storedStack.getCount();
				storedStack.setCount(Math.min(storedStack.getMaxStackSize(), storedStack.getCount() + itemToPickUp.getCount()));
				itemToPickUp.shrink(mergeLimit);

			}

			if(player != null)
				playInsertSound(player);
			setData(hollowItem, storedStack);
			return true;
		}

		return false;
	}

	public boolean canStore(ItemStack hollowItem, ItemStack itemToPickUp)
	{
		ItemStack storedStack = getData(hollowItem);
		return (storedStack.isEmpty() || (ItemStack.isSameItemSameComponents(storedStack, itemToPickUp) && storedStack.getCount() + itemToPickUp.getCount() <= storedStack.getMaxStackSize()));
	}

	public boolean isFull(ItemStack hollowItem)
	{
		ItemStack storedStack = getData(hollowItem);
		return !storedStack.isEmpty() && storedStack.getCount() >= storedStack.getMaxStackSize();
	}

	private void playInsertSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	private void playDropContentsSound(Entity entity) {
		entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		ItemStack rootStack = root.getItem();

		if(!isFull(rootStack))
		{
			Level level = root.getLevel();
			BlockPos pos = root.getBlockPos();
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, root.getBlockState().getShape(level, pos).bounds().move(pos));

			for (ItemEntity itemEntity : items)
			{
				ItemStack itemToPickUp = itemEntity.getItem();
				if (!itemEntity.isRemoved() && storeItem(null, rootStack, itemToPickUp) && itemToPickUp.getCount() <= 0)
				{
					itemEntity.discard();
					break;
				}
			}
		}
	}

	@Override
	public @Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult)
	{
		ItemStack heldItem = user.getItemInHand(hand);

		if(heldItem.isEmpty())
		{
			user.addItem(getData(root.getItem()));
			setData(root.getItem(), getDefaultData());
			return ItemInteractionResult.sidedSuccess(user.level().isClientSide());
		}

		return storeItem(user, root.getItem(), heldItem) ? ItemInteractionResult.sidedSuccess(user.level().isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled())
		{
			ItemStack stack = event.getItemStack();
			ItemStack storedStack = getData(stack);

			if(!stack.isEmpty())
			{
				if(event.getEntity().isShiftKeyDown())
				{
					ItemStack stackToDrop = storedStack.copy();
					stackToDrop.setCount(1);
					event.getEntity().drop(stackToDrop, true);
					storedStack.shrink(1);
					setData(stack, storedStack);
					playDropContentsSound(event.getEntity());
				}
				else dropItems(stack, storedStack, event.getEntity());
			}
		}
	}

	public boolean dropItems(ItemStack hollowItem, Entity user)
	{
		return dropItems(hollowItem, getData(hollowItem), user);
	}

	protected boolean dropItems(ItemStack hollowItem, ItemStack toDrop, Entity user)
	{
		if(toDrop.isEmpty())
			return false;

		if(user instanceof Player player)
			player.drop(toDrop, true);
		else if(nonPlayerDrop(user, toDrop, false, true) == null)
			return false;
		setData(hollowItem, ItemStack.EMPTY);
		playDropContentsSound(user);
		return true;
	}

	public static ItemEntity drop(Entity user, ItemStack droppedItem, boolean dropAround, boolean includeThrowerName)
	{
		if(user instanceof Player player)
			return player.drop(droppedItem, dropAround, includeThrowerName);
		else return nonPlayerDrop(user, droppedItem, dropAround, includeThrowerName);
	}

	public static ItemEntity nonPlayerDrop(Entity user, ItemStack droppedItem, boolean dropAround, boolean includeThrowerName) {
		if (droppedItem.isEmpty()) {
			return null;
		} else {
			if (user.level().isClientSide && user instanceof LivingEntity living) {
				living.swing(InteractionHand.MAIN_HAND);
			}

			double d0 = user.getEyeY() - 0.3F;
			ItemEntity itementity = new ItemEntity(user.level(), user.getX(), d0, user.getZ(), droppedItem);
			itementity.setPickUpDelay(40);
			if (includeThrowerName) {
				itementity.setThrower(user);
			}

			RandomSource random = user.getRandom();
			
			if (dropAround) {
				float f = random.nextFloat() * 0.5F;
				float f1 = random.nextFloat() * (float) (Math.PI * 2);
				itementity.setDeltaMovement((double)(-Mth.sin(f1) * f), 0.2F, (double)(Mth.cos(f1) * f));
			} else {
				float f7 = 0.3F;
				float f8 = Mth.sin(user.getXRot() * (float) (Math.PI / 180.0));
				float f2 = Mth.cos(user.getXRot() * (float) (Math.PI / 180.0));
				float f3 = Mth.sin(user.getYRot() * (float) (Math.PI / 180.0));
				float f4 = Mth.cos(user.getYRot() * (float) (Math.PI / 180.0));
				float f5 = random.nextFloat() * (float) (Math.PI * 2);
				float f6 = 0.02F * random.nextFloat();
				itementity.setDeltaMovement(
						(double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6,
						(double)(-f8 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
						(double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6
				);
			}

			if (!user.level().isClientSide)
				user.getCommandSenderWorld().addFreshEntity(itementity);
			return itementity;
		}
	}
	
	@Override
	public void onEntityItemDestroyed(ItemStack stack, Entity itemEntity, DamageSource damageSource)
	{
		ItemStack storedItem = getData(stack);
		if(!storedItem.isEmpty())
		{
			onContainerDestroyed(itemEntity, List.of(storedItem));
			setData(stack, getDefaultData());
		}
	}

	public static void onContainerDestroyed(Entity container, Iterable<ItemStack> contents) {
		Level level = container.level();
		if (!level.isClientSide) {
			contents.forEach(p_352858_ -> level.addFreshEntity(new ItemEntity(level, container.getX(), container.getY(), container.getZ(), p_352858_)));
		}
	}

	@Override
	public ItemStack readData(CompoundTag tag)
	{
		return tag.isEmpty() ? getDefaultData() : ItemStack.parse(CommonUtils.registryAccessStatic(), tag.getCompound("item")).orElse(getDefaultData());
	}

	@Override
	public CompoundTag writeData(ItemStack data) {
		return new CompoundTag() {{
			if(!data.isEmpty())
				put("item", data.save(CommonUtils.registryAccessStatic()));
		}};
	}

	@Override
	public ItemStack getDefaultData() {
		return ItemStack.EMPTY;
	}

	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		ItemStack storedStack = getData(stack);

		if(!storedStack.isEmpty())
			return Component.translatable("property.detail", name, Component.translatable("property.detail.item_count", storedStack.getHoverName(), storedStack.getCount())).withColor(getColor(stack));
		return name;
	}

	public boolean shrinkContents(ItemStack hollowItem, int amount)
	{
		ItemStack storedItem = getData(hollowItem);

		if(!hollowItem.isEmpty() && storedItem.getCount() >= amount)
		{
			storedItem.shrink(amount);
			setData(hollowItem, storedItem.isEmpty() ? ItemStack.EMPTY : storedItem);
			return true;
		}
		return false;
	}
}
