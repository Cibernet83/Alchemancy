package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record S2CUnlockCodexEntriesPacket(List<Holder<Property>> propertiesToUnlock) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CUnlockCodexEntriesPacket> STREAM_CODEC = StreamCodec.composite(
			Property.STREAM_CODEC.apply(ByteBufCodecs.list()),
			S2CUnlockCodexEntriesPacket::propertiesToUnlock,
			S2CUnlockCodexEntriesPacket::new
	);

	public S2CUnlockCodexEntriesPacket(ItemStack stack) {
		this(getPropertiesToUnlock(stack));
	}

	private static List<Holder<Property>> getPropertiesToUnlock(ItemStack stack) {

		List<Holder<Property>> result = new ArrayList<>();
		result.addAll(InfusedPropertiesHelper.getInfusedProperties(stack));
		result.addAll(InfusedPropertiesHelper.getInnateProperties(stack));
		return result;
	}

	public static final Type<S2CUnlockCodexEntriesPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/unlock_codex_entries"));

	public void handleDataOnMain(final IPayloadContext context) {
		Player player = context.player();
		for (Holder<Property> propertyHolder : propertiesToUnlock) {
			InfusionCodexSaveData.unlock(propertyHolder);
		}
	}

	@Override
	public Type<S2CUnlockCodexEntriesPacket> type() {
		return TYPE;
	}
}
