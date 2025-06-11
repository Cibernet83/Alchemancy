package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyPayloads
{
	@SubscribeEvent
	public static void registerPayloads(RegisterPayloadHandlersEvent event)
	{
		PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(S2CPlayFireworksPacket.TYPE, S2CPlayFireworksPacket.STREAM_CODEC, S2CPlayFireworksPacket::handleDataOnMain);
		registrar.playToClient(S2CPlayGustBasketEffectsPacket.TYPE, S2CPlayGustBasketEffectsPacket.STREAM_CODEC, S2CPlayGustBasketEffectsPacket::handleDataOnMain);
		registrar.playToClient(S2CAddPlayerMovementPacket.TYPE, S2CAddPlayerMovementPacket.STREAM_CODEC, S2CAddPlayerMovementPacket::handleDataOnMain);
		registrar.playToClient(EntitySyncTintColorS2CPayload.TYPE, EntitySyncTintColorS2CPayload.STREAM_CODEC, EntitySyncTintColorS2CPayload::handleDataOnMain);
	}
}
