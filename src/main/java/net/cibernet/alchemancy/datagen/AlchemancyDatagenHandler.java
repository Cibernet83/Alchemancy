package net.cibernet.alchemancy.datagen;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.advancements.criterion.DiscoverPropertyTrigger;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.advancements.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Alchemancy.MODID)
public class AlchemancyDatagenHandler
{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, fileHelper, List.of(
				AlchemancyDatagenHandler::getPropertyDiscoveryAdvancements,
				AlchemancyDatagenHandler::getAlchemancyMasterAdvancement
		)));

		generator.addProvider(event.includeClient(), new AlchemancyLangProvider(output));
		generator.addProvider(event.includeClient(), new CodexEntryProvider(lookupProvider, output));

		//generator.addProvider(event.includeClient(), new PropertyEntryProvider(output, lookupProvider, AlchemancyProperties.WARPED));
	}

	public static void getPropertyDiscoveryAdvancements(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper)
	{
		for (DeferredHolder<Property, ? extends Property> property : AlchemancyProperties.REGISTRY.getEntries()) {

			ResourceLocation key = property.value().getKey();
			Advancement.Builder builder = Advancement.Builder.recipeAdvancement();

			builder.parent(AdvancementSubProvider.createPlaceholder("alchemancy:discovery/root"));

			builder.display(
					InfusedPropertiesHelper.createPropertyCapsule(property),
					Component.translatable("property." + key.toLanguageKey()),
					Component.translatable("advancements.alchemancy:discovery.discover.description"),
					null,
					AdvancementType.TASK,
					false, false, true
			);

			builder.addCriterion("discover_property", DiscoverPropertyTrigger.TriggerInsance.discoverProperty(property));
			builder.requirements(AdvancementRequirements.allOf(List.of("discover_property")));


			builder.save(saver, ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "discovery/" + key.getPath()), existingFileHelper);
		}
	}

	private static final List<Holder<Property>> UNOBTAINABLE_PROPERTIES = List.of(
			AlchemancyProperties.CLAY_MOLD,
			AlchemancyProperties.SMITING,
			AlchemancyProperties.AUXILIARY,
			AlchemancyProperties.RANDOM,
			AlchemancyProperties.UNMOVABLE,
			AlchemancyProperties.WORLD_OBLITERATOR,
			AlchemancyProperties.BATTERY_POWERED,
			AlchemancyProperties.LIVING_BATTERY
	);

	public static void getAlchemancyMasterAdvancement(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper)
	{
		Advancement.Builder builder = Advancement.Builder.recipeAdvancement();

		builder.parent(AdvancementSubProvider.createPlaceholder("alchemancy:story/apply_property"));

		builder.display(
				AlchemancyItems.ALCHEMANCY_CATALYST.asItem(),
				Component.translatable("advancements.story.alchemancy:all_properties.title"),
				Component.translatable("advancements.story.alchemancy:all_properties.description"),
				null,
				AdvancementType.CHALLENGE,
				true, true, false
		);


		List<String> requirements = new ArrayList<>();
		for (DeferredHolder<Property, ? extends Property> property : AlchemancyProperties.REGISTRY.getEntries()) {

			if(UNOBTAINABLE_PROPERTIES.contains(property))
				continue;

			String key = "discover_" + property.get().getKey().toString();
			builder.addCriterion(key, DiscoverPropertyTrigger.TriggerInsance.discoverProperty(property));
			requirements.add(key);
		}
		builder.requirements(AdvancementRequirements.allOf(requirements));

		builder.rewards(AdvancementRewards.Builder.experience(1000));

		builder.save(saver, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "story/all_properties"), existingFileHelper);
	}

}
