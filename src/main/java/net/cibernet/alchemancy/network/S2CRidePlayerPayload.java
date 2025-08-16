package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record S2CRidePlayerPayload(int riderId) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CRidePlayerPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			S2CRidePlayerPayload::riderId,
			S2CRidePlayerPayload::new
	);

	public static final Type<S2CRidePlayerPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/ride_player"));

	public void handleDataOnMain(final IPayloadContext context) {

		var rider = context.player().level().getEntity(riderId());
		if(rider != null)
			rider.startRiding(context.player());
	}

	@Override
	public Type<S2CRidePlayerPayload> type() {
		return TYPE;
	}
}
