package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;

public class CraftyProperty extends Property
{
	private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");


	@Override
	public @Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {

		openCraftingMenu(user, root.getItem());
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		if(!event.isCanceled())
		{
			openCraftingMenu(event.getEntity(), event.getItemStack());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAE693C;
	}

	public static void openCraftingMenu(Player player, ItemStack sourceItem)
	{
		player.openMenu(getMenuProvider(sourceItem));
	}

	protected static MenuProvider getMenuProvider(final ItemStack sourceItem) {
		return new SimpleMenuProvider(
				(containerId, playerInventory, player) -> new CraftingMenu(containerId, playerInventory, new PlayerContainerLevelAccess(player)) {
					@Override
					public boolean stillValid(Player player) {
						return player.getInventory().getSelected().equals(sourceItem);
					}
				}, CONTAINER_TITLE
		);
	}

	public record PlayerContainerLevelAccess(Player player) implements ContainerLevelAccess
	{
		@Override
		public <T> @NotNull Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer)
		{
			return Optional.ofNullable(levelPosConsumer.apply(player.level(), player.blockPosition()));
		}
	}
}
