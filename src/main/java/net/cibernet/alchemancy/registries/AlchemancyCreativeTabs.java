package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;
import java.util.List;

import static net.cibernet.alchemancy.Alchemancy.MODID;
import static net.cibernet.alchemancy.registries.AlchemancyItems.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);



	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GENERAL = REGISTRY.register("alchemancy", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.alchemancy"))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> BLAZEBLOOM.get().getDefaultInstance())
			.displayItems((parameters, output) -> {
				output.accept(BLAZEBLOOM.get());
				output.accept(ALCHEMICAL_EXTRACT.get());
				output.accept(ALCHEMANCY_FORGE.get());
				output.accept(INFUSION_PEDESTAL.get());
				output.accept(ALCHEMANCY_CATALYST.get());

				output.accept(LEAD_INGOT.get());
				output.accept(LEAD_SWORD.get());
				output.accept(LEAD_SHOVEL.get());
				output.accept(LEAD_PICKAXE.get());
				output.accept(LEAD_AXE.get());
				output.accept(LEAD_HOE.get());
				output.accept(LEAD_HELMET.get());
				output.accept(LEAD_CHESTPLATE.get());
				output.accept(LEAD_LEGGINGS.get());
				output.accept(LEAD_BOOTS.get());
				
				output.accept(DREAMSTEEL_INGOT.get());
				output.accept(DREAMSTEEL_SWORD.get());
				output.accept(DREAMSTEEL_SHOVEL.get());
				output.accept(DREAMSTEEL_PICKAXE.get());
				output.accept(DREAMSTEEL_AXE.get());
				output.accept(DREAMSTEEL_HOE.get());

				output.accept(BLANK_PEARL.get());
				output.accept(REVEALING_PEARL.get());
				output.accept(PARADOX_PEARL.get());
				output.accept(GLOWING_ORB.get());

				output.accept(MICROSPACE_SINGULARITY.get());
				output.accept(MACROSPACE_SINGULARITY.get());

				output.accept(IRON_RING.get());
				output.accept(ETERNAL_GLOW_RING.get());
				output.accept(PHASING_RING.get());
				output.accept(UNDYING_RING.get());
				output.accept(FRIENDSHIP_RING.get());

			}).build());

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PROPERTIES = REGISTRY.register("alchemancy_properties", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.alchemancy_properties"))
			.withTabsBefore(GENERAL.getKey())
			.icon(() -> PROPERTY_CAPSULE.get().getDefaultInstance())
			.displayItems((parameters, output) -> {

				for (DeferredHolder<Property, ? extends Property> entry : AlchemancyProperties.REGISTRY.getEntries().stream().sorted(Comparator.comparing(DeferredHolder::getKey)).toList()) {
					output.acceptAll(entry.value().populateCreativeTab(PROPERTY_CAPSULE, entry));
				}

			}).build());

	@SubscribeEvent
	public static void addCreative(BuildCreativeModeTabContentsEvent event)
	{
		if(event.getTabKey().equals(CreativeModeTabs.COMBAT))
		{
			event.insertAfter(Items.GOLDEN_SWORD.getDefaultInstance(), LEAD_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(Items.NETHERITE_SWORD.getDefaultInstance(), DREAMSTEEL_SWORD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

			event.insertAfter(Items.GOLDEN_BOOTS.getDefaultInstance(), LEAD_HELMET.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_HELMET.get().getDefaultInstance(), LEAD_CHESTPLATE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_CHESTPLATE.get().getDefaultInstance(), LEAD_LEGGINGS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_LEGGINGS.get().getDefaultInstance(), LEAD_BOOTS.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))
		{
			event.insertAfter(Items.GOLDEN_HOE.getDefaultInstance(), LEAD_SHOVEL.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_SHOVEL.get().getDefaultInstance(), LEAD_PICKAXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_PICKAXE.get().getDefaultInstance(), LEAD_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_AXE.get().getDefaultInstance(), LEAD_HOE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

			event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), DREAMSTEEL_SHOVEL.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_SHOVEL.get().getDefaultInstance(), DREAMSTEEL_PICKAXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_PICKAXE.get().getDefaultInstance(), DREAMSTEEL_AXE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_AXE.get().getDefaultInstance(), DREAMSTEEL_HOE.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if(event.getTabKey().equals(CreativeModeTabs.INGREDIENTS))
		{
			event.insertAfter(Items.GOLD_INGOT.getDefaultInstance(), LEAD_INGOT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(Items.NETHERITE_INGOT.getDefaultInstance(), DREAMSTEEL_INGOT.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}
}
