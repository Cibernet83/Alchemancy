package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CAddPlayerMovementPayload(double x, double y, double z) implements CustomPacketPayload
{
	public static final StreamCodec<ByteBuf, S2CAddPlayerMovementPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPayload::x,
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPayload::y,
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPayload::z,
			S2CAddPlayerMovementPayload::new
	);

	public S2CAddPlayerMovementPayload(Vec3 delta) {
		this(delta.x(), delta.y(), delta.z());
	}

	public static final Type<S2CAddPlayerMovementPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/add_player_movement"));

	public  void handleDataOnMain(final IPayloadContext context) {
		// Do something with the data, on the main thread
		Player player = context.player();
		player.setDeltaMovement(player.getDeltaMovement().add(x(), y(), z()));
	}

	@Override
	public Type<S2CAddPlayerMovementPayload> type() {
		return TYPE;
	}
}
