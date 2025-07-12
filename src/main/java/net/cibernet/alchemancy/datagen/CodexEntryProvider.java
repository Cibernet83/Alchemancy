package net.cibernet.alchemancy.datagen;

import com.mojang.serialization.JsonOps;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.SparklingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CodexEntryProvider implements DataProvider {

	public static final HashMap<Holder<Property>, CodexEntryReloadListenener.CodexEntry> ENTRIES = new HashMap<>();
	private final CompletableFuture<HolderLookup.Provider> registries;
	private final PackOutput.PathProvider pathProvider;

	public CodexEntryProvider(CompletableFuture<HolderLookup.Provider> registries, PackOutput packOutput) {
		this.registries = registries;
		this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, CodexEntryReloadListenener.PATH);
	}

	public void populate() {

		for (Item item : BuiltInRegistries.ITEM) {
			if(item.components().has(AlchemancyItems.Components.INNATE_PROPERTIES.get()))
				item.components().get(AlchemancyItems.Components.INNATE_PROPERTIES.get()).forEachProperty(propertyHolder ->
						addInnateItem(propertyHolder, BuiltInRegistries.ITEM.wrapAsHolder(item)));
		}

		addRelatedProperties(AlchemancyProperties.FLAMMABLE, List.of(AlchemancyProperties.CHARRED));
		addRelatedProperties(AlchemancyProperties.ASSIMILATING, List.of(AlchemancyProperties.ASSEMBLING));
		addRelatedProperties(AlchemancyProperties.SANITIZED, List.of(AlchemancyProperties.INFECTED));
		addRelatedProperties(AlchemancyProperties.INFECTED, List.of(AlchemancyProperties.SANITIZED, AlchemancyProperties.DEAD));
		addRelatedProperties(AlchemancyProperties.SHATTERING, List.of(AlchemancyProperties.BRITTLE));

		addRelatedProperties(AlchemancyProperties.SADDLED, List.of(AlchemancyProperties.WEALTHY, AlchemancyProperties.SWEET));
		addRelatedProperties(AlchemancyProperties.SMELTING, List.of(AlchemancyProperties.HOLLOW, AlchemancyProperties.FLAMMABLE, AlchemancyProperties.CHARRED));
		addRelatedProperties(AlchemancyProperties.DRIPPING, List.of(AlchemancyProperties.HOLLOW, AlchemancyProperties.BUCKETING, AlchemancyProperties.CAPTURING, AlchemancyProperties.CALCAREOUS));
		addRelatedProperties(AlchemancyProperties.ABSORBING, List.of(AlchemancyProperties.BUCKETING));

		addRelatedProperties(AlchemancyProperties.MUSICAL, List.of(AlchemancyProperties.SPARKLING));
		addRelatedProperties(AlchemancyProperties.MYCELLIC, List.of(AlchemancyProperties.SPARKLING));
		addRelatedProperties(AlchemancyProperties.ROCKET_POWERED, List.of(AlchemancyProperties.SPARKLING));
		addRelatedProperties(AlchemancyProperties.AIR_WALKER, List.of(AlchemancyProperties.SPARKLING));
		addRelatedProperties(AlchemancyProperties.WAVE_RIDER, List.of(AlchemancyProperties.SPARKLING));
		addRelatedProperties(AlchemancyProperties.MAGNETIC, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.FERROUS));
		addRelatedProperties(AlchemancyProperties.KINETIC_GRAB, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
		addRelatedProperties(AlchemancyProperties.BLINKING, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
		addRelatedProperties(AlchemancyProperties.GUST_JET, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));
		addRelatedProperties(AlchemancyProperties.WORLD_OBLITERATOR, List.of(AlchemancyProperties.SPARKLING, AlchemancyProperties.EXTENDED));

		addRelatedProperties(AlchemancyProperties.MAGIC_RESISTANT, List.of(AlchemancyProperties.ARCANE));

		addRelatedProperties(AlchemancyProperties.EDIBLE, List.of(AlchemancyProperties.SWIFT, AlchemancyProperties.EXTENDED, AlchemancyProperties.SLUGGISH));
		addRelatedProperties(AlchemancyProperties.CEASELESS_VOID, List.of(AlchemancyProperties.SWIFT, AlchemancyProperties.EXTENDED, AlchemancyProperties.SLUGGISH));

		addRelatedProperties(AlchemancyProperties.ENERGIZED, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
		addRelatedProperties(AlchemancyProperties.CONDUCTIVE, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
		addRelatedProperties(AlchemancyProperties.INSULATED, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
		addRelatedProperties(AlchemancyProperties.WET, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));
		addRelatedProperties(AlchemancyProperties.FERROUS, List.of(AlchemancyProperties.SHOCKING, AlchemancyProperties.SMITING));

		addRelatedProperties(AlchemancyProperties.EXTENDED, List.of(AlchemancyProperties.GUST_JET, AlchemancyProperties.BLINKING, AlchemancyProperties.KINETIC_GRAB, AlchemancyProperties.WORLD_OBLITERATOR));
		addRelatedProperties(AlchemancyProperties.INFUSION_CODEX, List.of(AlchemancyProperties.REVEALED, AlchemancyProperties.REVEALING, AlchemancyProperties.AWAKENED));

		addRelatedProperties(AlchemancyProperties.AWKWARD, List.of(AlchemancyProperties.BLINDING, AlchemancyProperties.FIRE_RESISTANT, AlchemancyProperties.TIPSY, AlchemancyProperties.NOCTURNAL,
				AlchemancyProperties.SWIFT, AlchemancyProperties.SANITIZED, AlchemancyProperties.LEAPING, AlchemancyProperties.AQUATIC));
		addRelatedProperties(AlchemancyProperties.SOULBIND, List.of(AlchemancyProperties.VENGEFUL, AlchemancyProperties.VAMPIRIC, AlchemancyProperties.LOYAL, AlchemancyProperties.CAPTURING,
				AlchemancyProperties.PHASING, AlchemancyProperties.LIGHT_SEEKING, AlchemancyProperties.RELENTLESS, AlchemancyProperties.SPIRIT_BOND, AlchemancyProperties.ENERGY_SAPPER));

		addRelatedProperties(AlchemancyProperties.SPARKLING, SparklingProperty.getAllParticleProviders());
		addRelatedProperties(AlchemancyProperties.CLUELESS, AlchemancyProperties.REGISTRY.getEntries().stream().filter(p -> p.value() instanceof IDataHolder<?> dataHolder && dataHolder.cluelessCanReset()).collect(Collectors.toSet()));
	}

	public static void addRelatedProperties(Holder<Property> mainProperty, Collection<Holder<Property>> related) {
		//TODO
	}

	public static void addInnateItem(Holder<Property> propertyHolder, Holder<Item> innate) {
		if(CodexEntryProvider.ENTRIES.containsKey(propertyHolder))
			CodexEntryProvider.ENTRIES.get(propertyHolder).innates().add(innate);
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
