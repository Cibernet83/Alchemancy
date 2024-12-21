package net.cibernet.alchemancy.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PropertyEntryProvider implements DataProvider
{
	private final CompletableFuture<HolderLookup.Provider> registries;
	private final PackOutput.PathProvider pathProvider;
	private final String wikiLink;
	private final Collection<DeferredHolder<Property, ? extends Property>> entries;

	protected PropertyEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
	{
		this(output, registries, "https://github.com/Cibernet83/Alchemancy/wiki/Infusion-Properties#", AlchemancyProperties.REGISTRY.getEntries());
	}

	protected PropertyEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, DeferredHolder<Property, ? extends Property>... entries)
	{
		this(output, registries, "https://github.com/Cibernet83/Alchemancy/wiki/Infusion-Properties#", List.of(entries));
	}

	public PropertyEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String wikiLink, Collection<DeferredHolder<Property, ? extends Property>> entries)
	{
		this.registries = registries;
		this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "patchouli_books/alchemancer_journal/en_us/entries/properties");
		this.wikiLink = wikiLink;

		this.entries = entries.stream().filter(propertyDeferredHolder -> propertyDeferredHolder.get().hasJournalEntry()).toList();
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output)
	{
		return registries.thenCompose(lookup -> run(output, lookup));
	}


	protected CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries)
	{
		final List<CompletableFuture<?>> list = new ArrayList<>();

		for (DeferredHolder<Property, ? extends Property> property : entries)
			list.add(getEntryCompletable(output, property));

		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	public CompletableFuture<?> getEntryCompletable(final CachedOutput output, Holder<Property> property)
	{
		ResourceLocation id = property.value().getKey();

		JsonObject jsonElement = new JsonObject();
		jsonElement.addProperty("name", property.value().getLanguageKey());
		jsonElement.addProperty("icon","alchemancy:property_capsule[alchemancy:stored_properties=[\"" + id + "\"], alchemancy:ingredient_display={}]");
		jsonElement.addProperty("advancement", id.getNamespace() + ":discovery/" + id.getPath());
		jsonElement.addProperty("category", "alchemancy:properties");

//		JsonObject recipeMappings = new JsonObject(); no worky :(
//		recipeMappings.addProperty("alchemancy:property_capsule[alchemancy:stored_properties=[\"" + id + "\"]]", 0);
//		jsonElement.add("extra_recipe_mappings", recipeMappings);

		JsonArray pages = new JsonArray(2);

		JsonObject spotlightPage = new JsonObject();
		spotlightPage.addProperty("type", "patchouli:spotlight");
		spotlightPage.addProperty("title", property.value().getLanguageKey());
		spotlightPage.addProperty("item","alchemancy:property_capsule[alchemancy:stored_properties=[\"" + id + "\"], alchemancy:ingredient_display={}]");
		spotlightPage.addProperty("text", "alchemancy.entry.infusion_property.missing");
		pages.add(spotlightPage);

		JsonObject wikiPage = new JsonObject();
		wikiPage.addProperty("type", "patchouli:link");
		wikiPage.addProperty("url", wikiLink + id.getPath().replace("_", "-"));
		wikiPage.addProperty("link_text", "alchemancy.entry.infusion_property.wiki_button");
		wikiPage.addProperty("text", "alchemancy.entry.infusion_property.wiki");
		pages.add(wikiPage);

		jsonElement.add("pages", pages);

		return DataProvider.saveStable(output, jsonElement, pathProvider.json(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, id.getNamespace() + "/" + id.getPath())));
	}

	@Override
	public String getName() {
		return "Infusion Property Journal Entries";
	}
}
