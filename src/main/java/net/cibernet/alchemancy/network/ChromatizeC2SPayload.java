package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChromatizeC2SPayload(int tintColor) implements CustomPacketPayload {
	public static final Type<ChromatizeC2SPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "c2s/chromatize"));
	public static final StreamCodec<ByteBuf, ChromatizeC2SPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ChromatizeC2SPayload::tintColor,
			ChromatizeC2SPayload::new
	);


	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleDataOnMain(ChromatizeC2SPayload payload, IPayloadContext context) {

		ItemStack stack = context.player().getMainHandItem();
		if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.TINTED))
			InfusedPropertiesHelper.addProperty(stack, AlchemancyProperties.TINTED);
		CommonUtils.applyChromaTint(stack, payload.tintColor());
	}
}