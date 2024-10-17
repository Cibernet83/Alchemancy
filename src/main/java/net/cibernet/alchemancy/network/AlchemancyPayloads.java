package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AlchemancyPayloads
{
	public static void registerPayloads(RegisterPayloadHandlersEvent event)
	{
		PayloadRegistrar registrar = event.registrar("1");
		playToClient(registrar, "my_data", ClientboundPayload.STREAM_CODEC);
	}

	private static void playToClient(PayloadRegistrar registrar, String key, StreamCodec<ByteBuf, ClientboundPayload> codec)
	{
		registrar.playToClient(new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, key)), codec, ClientboundPayload::handleDataOnMain);
	}
}
