package net.cibernet.alchemancy.network;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record S2CDeathWardEffectsPayload(int entityId, ItemStack stack) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CDeathWardEffectsPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			S2CDeathWardEffectsPayload::entityId,
			ItemStack.STREAM_CODEC,
			S2CDeathWardEffectsPayload::stack,
			S2CDeathWardEffectsPayload::new
	);

	public S2CDeathWardEffectsPayload(Entity entity, ItemStack stack) {
		this(entity.getId(), stack);
	}

	public static final Type<S2CDeathWardEffectsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "s2c/death_ward_effects"));

	public void handleDataOnMain(final IPayloadContext context) {
		Level level = context.player().level();
		Entity entity = level.getEntity(entityId());
		Minecraft minecraft = Minecraft.getInstance();

		if (entity == null) return;

		minecraft.particleEngine.createTrackingEmitter(entity, SparklingProperty.getParticles(stack).orElse(ParticleTypes.TOTEM_OF_UNDYING), 30);
		level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F, false);
		if (entity == context.player()) {
			minecraft.gameRenderer.displayItemActivation(stack);
		}
	}

	@Override
	public Type<S2CDeathWardEffectsPayload> type() {
		return TYPE;
	}
}
