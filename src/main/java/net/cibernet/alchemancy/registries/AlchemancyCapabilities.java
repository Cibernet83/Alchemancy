package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.BatteryPoweredProperty;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AlchemancyCapabilities {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlock(
				Capabilities.ItemHandler.BLOCK, // capability to register for
				(level, pos, state, blockEntity, side) -> blockEntity instanceof ItemStackHolderBlockEntity pedestal ? pedestal.wrapper : null,
				AlchemancyBlocks.INFUSION_PEDESTAL.get(),
				AlchemancyBlocks.ALCHEMANCY_FORGE.get()
		);

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, AlchemancyBlockEntities.ROOTED_ITEM.get(), (blockEntity, direction) ->
				blockEntity.getItem().getCapability(Capabilities.EnergyStorage.ITEM));

		var items = BuiltInRegistries.ITEM.stream().toArray(Item[]::new);

		event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.BATTERY_POWERED) ?
				new ComponentEnergyStorage(stack, AlchemancyItems.Components.FE_STORAGE.get(), BatteryPoweredProperty.CAPACITY) :
				null, items);
	}
}
