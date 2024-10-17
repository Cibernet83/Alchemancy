package net.cibernet.alchemancy.properties;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class SaddledProperty extends Property
{
	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

		if(event.getEntity().startRiding(event.getTarget()))
		{
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDA662C;
	}
}
