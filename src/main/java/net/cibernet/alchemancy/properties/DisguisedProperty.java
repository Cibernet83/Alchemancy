package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class DisguisedProperty extends Property implements IDataHolder<ItemStack> {

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(event.getHand() == InteractionHand.MAIN_HAND)
		{
			ItemStack disguise = event.getEntity().getOffhandItem();
			if(getData(stack).isEmpty() && !disguise.isEmpty())
			{
				setData(stack, disguise);
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public void onStackedOverMe(ItemStack disguise, ItemStack stack, Player player, ClickAction clickAction, ItemStackedOnOtherEvent event)
	{
		if(clickAction == ClickAction.SECONDARY)
		{
			if(getData(stack).isEmpty() && !disguise.isEmpty())
			{
				setData(stack, disguise);
				event.setCanceled(true);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x924B29;
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
		ItemStack disguise = getData(stack);

		if(!disguise.isEmpty())
			return Component.translatable("property.detail", name, disguise.getHoverName()).withColor(getColor(stack));
		return name;
	}
}
