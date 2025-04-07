package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

public class UnmovableProperty extends Property
{
	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity)
	{
		InfusedPropertiesHelper.removeProperty(stack, asHolder());
	}

	@Override
	public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult) {

		return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xC1C100;
	}
}
