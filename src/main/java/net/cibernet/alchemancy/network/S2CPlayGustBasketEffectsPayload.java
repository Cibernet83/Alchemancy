package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.GustBasketBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CPlayGustBasketEffectsPayload(BlockPos pos, double distance) implements CustomPacketPayload
{
	public static final StreamCodec<ByteBuf, S2CPlayGustBasketEffectsPayload> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			S2CPlayGustBasketEffectsPayload::pos,
			ByteBufCodecs.DOUBLE,
			S2CPlayGustBasketEffectsPayload::distance,
			S2CPlayGustBasketEffectsPayload::new
	);

	public static final Type<S2CPlayGustBasketEffectsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/play_gust_basket_effects"));

	public  void handleDataOnMain(final IPayloadContext context) {
		GustBasketBlock.playGustEffects(context.player().level(), pos(), distance());
	}

	@Override
	public Type<S2CPlayGustBasketEffectsPayload> type() {
		return TYPE;
	}
}
