package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.client.screen.InfusionCodexIndexScreen;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class InfusionCodexProperty extends Property {

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		if (!event.isCanceled()) {
			if (event.getLevel().isClientSide())
				Minecraft.getInstance().setScreen(new InfusionCodexIndexScreen(event.getItemStack().getDisplayName()));
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xAD101C;
	}
}
