package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;

public class StonecuttingProperty extends Property {
	private static final Component CONTAINER_TITLE = Component.translatable("container.stonecutter");

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {

		if (!event.isCanceled()) {
			openStonecuttingMenu(event.getEntity(), event.getItemStack());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public void onRightClickBlock(UseItemOnBlockEvent event) {

		Player player = event.getPlayer();

		if (event.getLevel().isClientSide() || event.isCanceled() || (player != null && player.isShiftKeyDown()) || !InfusedPropertiesHelper.hasProperty(event.getItemStack(), AlchemancyProperties.INTERACTABLE))
			return;

		Level level = event.getLevel();
		BlockPos blockPos = event.getPos();
		BlockState blockState = level.getBlockState(blockPos);

		var recipes = level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, new SingleRecipeInput(blockState.getBlock().asItem().getDefaultInstance()), level);
		Collections.shuffle(recipes);

		while (!recipes.isEmpty()) {
			var item = recipes.getFirst().value().getResultItem(level.registryAccess());

			if (item.getItem() instanceof BlockItem blockItem) {
				var resultState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(event.getUseOnContext()));
				if (resultState != null) {
					level.destroyBlock(blockPos, false, null);
					level.setBlock(blockPos, resultState, Block.UPDATE_ALL);

					if (level instanceof ServerLevel serverLevel)
						event.getItemStack().hurtAndBreak(
								1,
								serverLevel,
								player,
								p_348383_ -> {
									if (player != null)
										player.onEquippedItemBroken(p_348383_, event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
								}
						);

					var center = blockPos.getCenter();
					level.playSound(null, center.x, center.y, center.z, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
					
					event.setCanceled(true);
					event.setCancellationResult(ItemInteractionResult.SUCCESS);
					return;
				}
			}

			recipes.removeFirst();
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(0.2f, 0xA8A8A8, 0xD8D8D8);
	}

	public static void openStonecuttingMenu(Player player, ItemStack sourceItem) {
		player.openMenu(getMenuProvider(sourceItem));
	}

	protected static MenuProvider getMenuProvider(final ItemStack sourceItem) {
		return new SimpleMenuProvider(
				(containerId, playerInventory, player) -> new StonecutterMenu(containerId, playerInventory, new PlayerContainerLevelAccess(player)) {
					@Override
					public boolean stillValid(Player player) {
						return player.getInventory().getSelected().equals(sourceItem);
					}
				}, CONTAINER_TITLE
		);
	}

	public record PlayerContainerLevelAccess(Player player) implements ContainerLevelAccess {
		@Override
		public <T> @NotNull Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosConsumer) {
			return Optional.ofNullable(levelPosConsumer.apply(player.level(), player.blockPosition()));
		}
	}
}
