package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CPlayFireworksPayload(Fireworks fireworks, double x, double y, double z) implements CustomPacketPayload
{
	public static final StreamCodec<ByteBuf, S2CPlayFireworksPayload> STREAM_CODEC = StreamCodec.composite(
			Fireworks.STREAM_CODEC,
			S2CPlayFireworksPayload::fireworks,
			ByteBufCodecs.DOUBLE,
			S2CPlayFireworksPayload::x,
			ByteBufCodecs.DOUBLE,
			S2CPlayFireworksPayload::y,
			ByteBufCodecs.DOUBLE,
			S2CPlayFireworksPayload::z,
			S2CPlayFireworksPayload::new
	);

	public S2CPlayFireworksPayload(Fireworks fireworks, Vec3 position) {
		this(fireworks, position.x(), position.y(), position.z());
	}

	public static final CustomPacketPayload.Type<S2CPlayFireworksPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/play_fireworks"));

	public  void handleDataOnMain(final IPayloadContext context) {
		// Do something with the data, on the main thread
		context.player().level().createFireworks(x, y, z, 0, 0, 0, fireworks.explosions());
	}

	@Override
	public Type<S2CPlayFireworksPayload> type() {
		return TYPE;
	}
}
