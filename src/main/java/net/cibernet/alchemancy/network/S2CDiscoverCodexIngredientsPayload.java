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

public record S2CDiscoverCodexIngredientsPayload(List<ResourceLocation> itemsToDiscover) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CDiscoverCodexIngredientsPayload> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
			S2CDiscoverCodexIngredientsPayload::itemsToDiscover,
			S2CDiscoverCodexIngredientsPayload::new
	);

	public static final Type<S2CDiscoverCodexIngredientsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/discover_codex_ingredients"));

	public void handleDataOnMain(final IPayloadContext context) {
		for (ResourceLocation item : itemsToDiscover) {
			InfusionCodexSaveData.discoverItem(item);
		}
	}

	@Override
	public Type<S2CDiscoverCodexIngredientsPayload> type() {
		return TYPE;
	}
}
