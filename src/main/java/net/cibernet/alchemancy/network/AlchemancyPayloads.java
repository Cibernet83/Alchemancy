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
	}
}
