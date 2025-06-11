package net.cibernet.alchemancy.network;

import io.netty.buffer.ByteBuf;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record EntitySyncTintColorS2CPayload(int entityId, List<Integer> tintColor) implements CustomPacketPayload {
	public static final Type<EntitySyncTintColorS2CPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/attachment/entity_sync_tint_color"));
	public static final StreamCodec<ByteBuf, EntitySyncTintColorS2CPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, EntitySyncTintColorS2CPayload::entityId,
			ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), EntitySyncTintColorS2CPayload::tintColor,
			EntitySyncTintColorS2CPayload::new
	);

	public EntitySyncTintColorS2CPayload(Entity entity) {
		this(entity.getId(), entity.getData(AlchemancyDataAttachments.ENTITY_TINT));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleDataOnMain(EntitySyncTintColorS2CPayload payload, IPayloadContext context) {
		var entity = context.player().level().getEntity(payload.entityId());
		if (entity == null) {
			return;
		}
		entity.setData(AlchemancyDataAttachments.ENTITY_TINT, payload.tintColor());
	}
}