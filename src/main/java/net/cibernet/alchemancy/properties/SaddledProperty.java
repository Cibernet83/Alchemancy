package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.network.S2CRidePlayerPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class SaddledProperty extends Property
{
	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {

		var user = event.getEntity();
		if(user != event.getTarget() && user.startRiding(event.getTarget()))
		{
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);

			if(event.getTarget() instanceof ServerPlayer player)
				PacketDistributor.sendToPlayer(player, new S2CRidePlayerPayload(user.getId()));
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDA662C;
	}
}
