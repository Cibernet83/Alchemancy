package net.cibernet.alchemancy;

import net.cibernet.alchemancy.client.render.AlchemancyCatalystRenderer;
import net.cibernet.alchemancy.client.render.ItemStackHolderRenderer;
import net.cibernet.alchemancy.client.render.RootedItemRenderer;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancyEntities;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

import static net.cibernet.alchemancy.Alchemancy.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AlchemancyClient
{
	@SubscribeEvent
	public static void defineModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(AlchemancyCatalystRenderer.LAYER_LOCATION, AlchemancyCatalystRenderer::createBodyLayer);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		BlockEntityRenderers.register(AlchemancyBlockEntities.ITEMSTACK_HOLDER.get(), ItemStackHolderRenderer::new);
		BlockEntityRenderers.register(AlchemancyBlockEntities.ROOTED_ITEM.get(), RootedItemRenderer::new);
		BlockEntityRenderers.register(AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get(), AlchemancyCatalystRenderer::new);

		EntityRenderers.register(AlchemancyEntities.ITEM_PROJECTILE.get(), ThrownItemRenderer::new);
		EntityRenderers.register(AlchemancyEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

		ResourceLocation toggled = ResourceLocation.fromNamespaceAndPath(MODID, "toggled");
		for (Item toggleableItem : InnatePropertyItem.TOGGLEABLE_ITEMS)
			ItemProperties.register(toggleableItem, toggled, ((stack, level, entity, seed) -> {
				return AlchemancyProperties.TOGGLEABLE.get().getData(stack) ? 1 : 0;
			}));

	}

	@SubscribeEvent
	public static void initItemColors(RegisterColorHandlersEvent.Item event)
	{
		event.register(((stack, tintIndex) ->
		{
			List<Integer> colors = stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties().stream().map(propertyHolder -> propertyHolder.value().getColor(stack)).toList();
			return colors.isEmpty() ? -1 : FastColor.ARGB32.color(255, colors.get((int) Math.abs((System.currentTimeMillis() / 2000) % colors.size())));
		}), AlchemancyItems.PROPERTY_CAPSULE);
	}
}
