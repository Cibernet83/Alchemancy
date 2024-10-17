package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundPayload(int foo) implements CustomPacketPayload
{
	public static final StreamCodec<ByteBuf, ClientboundPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			ClientboundPayload::foo,
			ClientboundPayload::new
	);

	public static final CustomPacketPayload.Type<ClientboundPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "my_data"));

	public void handleDataOnMain(final IPayloadContext context) {
		// Do something with the data, on the main thread
		Alchemancy.LOGGER.info("{}", foo());
	}

	@Override
	public Type<ClientboundPayload> type() {
		return TYPE;
	}

	public static class PayloadType
	{

		public StreamCodec<ByteBuf, ClientboundPayload> getStreamCodec()
		{
			return STREAM_CODEC;
		}

		public Type<ClientboundPayload> type() {
			return TYPE;
		}
	}
}
