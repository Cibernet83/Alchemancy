package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record S2CUnlockCodexEntriesPayload(List<Holder<Property>> propertiesToUnlock) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CUnlockCodexEntriesPayload> STREAM_CODEC = StreamCodec.composite(
			Property.STREAM_CODEC.apply(ByteBufCodecs.list()),
			S2CUnlockCodexEntriesPayload::propertiesToUnlock,
			S2CUnlockCodexEntriesPayload::new
	);

	public S2CUnlockCodexEntriesPayload(ItemStack stack) {
		this(InfusionCodexSaveData.getPropertiesToUnlock(stack));
	}



	public static final Type<S2CUnlockCodexEntriesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/unlock_codex_entries"));

	public void handleDataOnMain(final IPayloadContext context) {
		for (Holder<Property> propertyHolder : propertiesToUnlock) {
			InfusionCodexSaveData.unlock(propertyHolder);
		}
	}

	@Override
	public Type<S2CUnlockCodexEntriesPayload> type() {
		return TYPE;
	}
}
