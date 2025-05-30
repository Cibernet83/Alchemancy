package net.cibernet.alchemancy.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CodexEntryReloadListenener implements ResourceManagerReloadListener {

	private static final Gson GSON_INSTANCE =  new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final HashMap<Holder<Property>, CodexEntry> ENTRIES = new HashMap<>();

	public static final CodexEntryReloadListenener INSTANCE = new CodexEntryReloadListenener();

	public static final String PATH = Alchemancy.MODID + "/codex_entries";

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {

		ENTRIES.clear();

		resourceManager.listResources(PATH, (r) -> r.getPath().endsWith(".json")).forEach(((location, resource) ->
		{
			Optional<Holder.Reference<Property>> propertyHolder = AlchemancyProperties.REGISTRY.getRegistry().get().getHolder(ResourceLocation.fromNamespaceAndPath(location.getNamespace(),
					location.getPath().substring(PATH.length() + 1, location.getPath().lastIndexOf(".json"))));

			if(propertyHolder.isEmpty()) return;

			try {
				InputStream inputStream = resource.open();
				Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				JsonObject json = GsonHelper.fromJson(GSON_INSTANCE, reader, JsonElement.class).getAsJsonObject();

				ENTRIES.put(propertyHolder.get(), CodexEntry.CODEC.decode(JsonOps.INSTANCE, json).getPartialOrThrow().getFirst());

			} catch (IOException | IndexOutOfBoundsException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	public static HashMap<Holder<Property>, CodexEntry> getEntries() {
		return ENTRIES;
	}

	public record CodexEntry(Component flavor, List<PropertyFunction> functions) {


		public static final Codec<CodexEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ComponentSerialization.CODEC.optionalFieldOf("flavor", Component.empty()).forGetter(CodexEntry::flavor),
				PropertyFunction.CODEC.listOf().fieldOf("functions").forGetter(CodexEntry::functions)
		).apply(instance, CodexEntry::new));
	}
}
