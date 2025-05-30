package net.cibernet.alchemancy.datagen;

import com.mojang.serialization.JsonOps;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CodexEntryProvider implements DataProvider {

	public static final HashMap<Holder<Property>, CodexEntryReloadListenener.CodexEntry> ENTRIES = new HashMap<>();
	private final CompletableFuture<HolderLookup.Provider> registries;
	private final PackOutput.PathProvider pathProvider;

	public CodexEntryProvider(CompletableFuture<HolderLookup.Provider> registries, PackOutput packOutput) {
		this.registries = registries;
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, CodexEntryReloadListenener.PATH);
	}

	public void populate() {

	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		populate();
		return registries.thenCompose(lookup -> runLater(output));
	}


	protected CompletableFuture<?> runLater(final CachedOutput output) {
		final List<CompletableFuture<?>> list = new ArrayList<>();

		for (Map.Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry> entry : ENTRIES.entrySet())
			list.add(getEntryCompletable(output, entry.getKey(), entry.getValue()));

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	private CompletableFuture<?> getEntryCompletable(CachedOutput output, Holder<Property> propertyHolder, CodexEntryReloadListenener.CodexEntry codexEntry) {

		var key = propertyHolder.getKey().location();

		return DataProvider.saveStable(output, CodexEntryReloadListenener.CodexEntry.CODEC.encodeStart(JsonOps.INSTANCE, codexEntry).getOrThrow(),
				pathProvider.json(ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath())));
	}

	@Override
	public String getName() {
		return "Infusion Codex Entries";
	}
}
