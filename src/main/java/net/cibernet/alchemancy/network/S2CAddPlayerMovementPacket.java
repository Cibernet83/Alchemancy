package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CAddPlayerMovementPacket(double x, double y, double z) implements CustomPacketPayload
{
	public static final StreamCodec<ByteBuf, S2CAddPlayerMovementPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPacket::x,
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPacket::y,
			ByteBufCodecs.DOUBLE,
			S2CAddPlayerMovementPacket::z,
			S2CAddPlayerMovementPacket::new
	);

	public S2CAddPlayerMovementPacket(Vec3 delta) {
		this(delta.x(), delta.y(), delta.z());
	}

	public static final Type<S2CAddPlayerMovementPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/add_player_movement"));

	public  void handleDataOnMain(final IPayloadContext context) {
		// Do something with the data, on the main thread
		Player player = context.player();
		player.setDeltaMovement(player.getDeltaMovement().add(x(), y(), z()));
	}

	@Override
	public Type<S2CAddPlayerMovementPacket> type() {
		return TYPE;
	}
}
