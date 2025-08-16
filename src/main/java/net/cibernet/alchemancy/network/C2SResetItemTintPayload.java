package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record C2SResetItemTintPayload() implements CustomPacketPayload {
	public static final Type<C2SResetItemTintPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "c2s/reset_tint"));
	public static final StreamCodec<ByteBuf, C2SResetItemTintPayload> STREAM_CODEC = StreamCodec.unit(new C2SResetItemTintPayload());


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleDataOnMain(C2SResetItemTintPayload payload, IPayloadContext context) {

		InfusedPropertiesHelper.removeProperty(context.player().getMainHandItem(), AlchemancyProperties.TINTED);
	}
}