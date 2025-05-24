package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;

import static net.cibernet.alchemancy.Alchemancy.MODID;
import static net.cibernet.alchemancy.registries.AlchemancyItems.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyCreativeTabs
{
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);



	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GENERAL = REGISTRY.register("alchemancy", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.alchemancy"))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> BLAZEBLOOM.toStack())
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
				output.accept(DREAMSTEEL_NUGGET.get());
				output.accept(DREAMSTEEL_SWORD.get());
				output.accept(DREAMSTEEL_SHOVEL.get());
				output.accept(DREAMSTEEL_PICKAXE.get());
				output.accept(DREAMSTEEL_AXE.get());
				output.accept(DREAMSTEEL_HOE.get());
				output.accept(DREAMSTEEL_BOW.get());
				output.accept(INFUSION_FLASK.get());

				output.accept(BLANK_PEARL.get());
				output.accept(REVEALING_PEARL.get());
				output.accept(PARADOX_PEARL.get());
				output.accept(VOID_PEARL.get());
				output.accept(ENTANGLED_SINGULARITY.get());

				output.accept(MICROSCOPIC_LENS.get());
				output.accept(MACROSCOPIC_LENS.get());

				output.accept(GLOWING_ORB.get());

				output.accept(IRON_RING.get());
				output.accept(ETERNAL_GLOW_RING.get());
				output.accept(PHASING_RING.get());
				output.accept(UNDYING_RING.get());
				output.accept(FRIENDSHIP_RING.get());
				output.accept(VOIDLESS_RING.get());
				output.accept(SPARKLING_BAND.get());

				output.accept(PROPERTY_VISOR.get());

				output.accept(LEADEN_APPLE.get());
				output.accept(LEADEN_CLOTH.get());
				output.accept(WAYWARD_MEDALLION.get());
				output.accept(BINDING_KEY.get());

				output.accept(ROCKET_POWERED_HAMMER.get());
				output.accept(HOME_RUN_BAT.get());
				output.accept(FERAL_BLADE.get());

				output.accept(BLACK_HOLE_PICKAXE.get());
				output.accept(BLACK_HOLE_AXE.get());
				output.accept(BLACK_HOLE_SHOVEL.get());
				output.accept(BLACK_HOLE_HOE.get());

				output.accept(POCKET_BLACK_HOLE.get());
				output.accept(CEASELESS_VOID_BAG.get());

			}).build());

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> PROPERTIES = REGISTRY.register("alchemancy_properties", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.alchemancy_properties"))
			.withTabsBefore(GENERAL.getKey())
			.icon(() -> PROPERTY_CAPSULE.toStack())
			.displayItems((parameters, output) -> {

				for (DeferredHolder<Property, ? extends Property> entry : AlchemancyProperties.REGISTRY.getEntries().stream().sorted(Comparator.comparing(DeferredHolder::getKey)).toList()) {
					output.acceptAll(entry.value().populateCreativeTab(PROPERTY_CAPSULE, entry));
				}

			}).build());

	@SubscribeEvent
	public static void addCreative(BuildCreativeModeTabContentsEvent event)
	{
		if(event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS))
		{
			event.insertAfter(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), LEADEN_APPLE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		if(event.getTabKey().equals(CreativeModeTabs.COMBAT))
		{
			event.insertAfter(Items.GOLDEN_SWORD.getDefaultInstance(), LEAD_SWORD.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(Items.NETHERITE_SWORD.getDefaultInstance(), DREAMSTEEL_SWORD.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_SWORD.toStack(), FERAL_BLADE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			
			event.insertAfter(Items.BOW.getDefaultInstance(), DREAMSTEEL_BOW.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

			event.insertAfter(Items.MACE.getDefaultInstance(), ROCKET_POWERED_HAMMER.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(ROCKET_POWERED_HAMMER.toStack(), HOME_RUN_BAT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

			event.insertAfter(Items.GOLDEN_BOOTS.getDefaultInstance(), LEAD_HELMET.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_HELMET.toStack(), LEAD_CHESTPLATE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_CHESTPLATE.toStack(), LEAD_LEGGINGS.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_LEGGINGS.toStack(), LEAD_BOOTS.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))
		{
			event.insertAfter(Items.GOLDEN_HOE.getDefaultInstance(), LEAD_SHOVEL.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_SHOVEL.toStack(), LEAD_PICKAXE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_PICKAXE.toStack(), LEAD_AXE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(LEAD_AXE.toStack(), LEAD_HOE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

			event.insertAfter(Items.NETHERITE_HOE.getDefaultInstance(), DREAMSTEEL_SHOVEL.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_SHOVEL.toStack(), DREAMSTEEL_PICKAXE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_PICKAXE.toStack(), DREAMSTEEL_AXE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(DREAMSTEEL_AXE.toStack(), DREAMSTEEL_HOE.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
		else if(event.getTabKey().equals(CreativeModeTabs.INGREDIENTS))
		{
			event.insertAfter(Items.GOLD_INGOT.getDefaultInstance(), LEAD_INGOT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(Items.NETHERITE_INGOT.getDefaultInstance(), DREAMSTEEL_INGOT.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			event.insertAfter(Items.GOLD_NUGGET.getDefaultInstance(), DREAMSTEEL_NUGGET.toStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}
}
