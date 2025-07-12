package net.cibernet.alchemancy.datagen;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AlchemancyPropertyTagsProvider extends IntrinsicHolderTagsProvider<Property> {

	public AlchemancyPropertyTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, AlchemancyProperties.REGISTRY_KEY, lookupProvider, (block) -> block.asHolder().getKey(), Alchemancy.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {

//		var properties = AlchemancyProperties.REGISTRY.getEntries().stream()
//				.filter(p -> !CodexEntryProvider.ENTRIES.containsKey(p) && !AlchemancyDatagenHandler.UNINFUSABLE_PROPERTIES.contains(p))
//				.map(DeferredHolder::value).toArray(Property[]::new);
//
//        tag(TagKey.create(AlchemancyProperties.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "missing_codex_entries")))
//		        .add(properties);
	}
}