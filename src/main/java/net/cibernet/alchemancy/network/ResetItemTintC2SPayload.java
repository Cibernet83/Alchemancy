package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResetItemTintC2SPayload() implements CustomPacketPayload {
	public static final Type<ResetItemTintC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "c2s/reset_tint"));
	public static final StreamCodec<ByteBuf, ResetItemTintC2SPayload> STREAM_CODEC = StreamCodec.unit(new ResetItemTintC2SPayload());


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleDataOnMain(ResetItemTintC2SPayload payload, IPayloadContext context) {

		InfusedPropertiesHelper.removeProperty(context.player().getMainHandItem(), AlchemancyProperties.TINTED);
	}
}