package net.cibernet.alchemancy.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.function.BiFunction;

public class CraftyProperty extends Property
{
	private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		if(!event.isCanceled())
		{
			openCraftingMenu(event.getEntity());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

		if(!event.isCanceled())
		{
			openCraftingMenu(event.getEntity());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAE693C;
	}


	public static void openCraftingMenu(Player player)
	{
		player.openMenu(getMenuProvider());
	}

	protected static MenuProvider getMenuProvider() {
		return new SimpleMenuProvider(
				(p_52229_, p_52230_, p_52231_) -> new CraftingMenu(p_52229_, p_52230_, new ContainerLevelAccess() {
					@Override
					public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
						return Optional.empty();
					}
				}), CONTAINER_TITLE
		);
	}
}
