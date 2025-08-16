package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record S2CInventoryTickPayload(int entityId, List<SlotEntry> items,
                                      int selectedSlot) implements CustomPacketPayload {

	public static final Type<S2CInventoryTickPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/inventory_tick"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CInventoryTickPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, S2CInventoryTickPayload::entityId,
			SlotEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CInventoryTickPayload::items,
			ByteBufCodecs.VAR_INT, S2CInventoryTickPayload::selectedSlot,
			S2CInventoryTickPayload::new
	);

	public static void handleDataOnMain(S2CInventoryTickPayload payload, IPayloadContext context) {
		var entity = context.player().level().getEntity(payload.entityId());
		if (entity == null) return;

		for (SlotEntry item : payload.items) {
			InfusedPropertiesHelper.forEachProperty(item.stack(), propertyHolder -> propertyHolder.value().onInventoryTick(entity, item.stack(), entity.level(), item.slot(), item.slot() == payload.selectedSlot()));
		}
	}

	public static void sendPacket(ServerPlayer player) {

		var inventory = player.getInventory().items;
		PacketDistributor.sendToPlayersTrackingEntity(player, new S2CInventoryTickPayload(player.getId(), inventory.stream()
				.map(stack -> new SlotEntry(inventory.indexOf(stack), stack))
				.filter(stack ->  !stack.stack().isEmpty()).toList(), player.getInventory().selected));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private record SlotEntry(int slot, ItemStack stack) {
		private static final StreamCodec<RegistryFriendlyByteBuf, SlotEntry> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, SlotEntry::slot,
				ItemStack.OPTIONAL_STREAM_CODEC, SlotEntry::stack,
				SlotEntry::new
		);
	}
}
