package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.Alchemancy;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AlchemancyDataAttachments {

	public static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Alchemancy.MODID);
	public static final Supplier<AttachmentType<List<Integer>>> ENTITY_TINT = register("entity_tint", ArrayList::new, Codec.INT.listOf());

	private static <T> Supplier<AttachmentType<T>> register(String key, Supplier<T> defaultValue, Codec<T> codec) {
		return REGISTRY.register(key, () -> AttachmentType.builder(defaultValue).serialize(codec).build());
	}
}
