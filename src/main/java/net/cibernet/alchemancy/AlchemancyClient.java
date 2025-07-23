package net.cibernet.alchemancy;

import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.client.render.AlchemancyCatalystItemRenderer;
import net.cibernet.alchemancy.client.render.AlchemancyCatalystRenderer;
import net.cibernet.alchemancy.client.render.ItemStackHolderRenderer;
import net.cibernet.alchemancy.client.render.RootedItemRenderer;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.IncreaseInfuseSlotsProperty;
import net.cibernet.alchemancy.registries.*;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

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

		EntityRenderers.register(AlchemancyEntities.INFUSION_FLASK.get(), ThrownItemRenderer::new);
		EntityRenderers.register(AlchemancyEntities.ITEM_PROJECTILE.get(), ThrownItemRenderer::new);
		EntityRenderers.register(AlchemancyEntities.FALLING_BLOCK.get(), FallingBlockRenderer::new);

		ResourceLocation toggled = ResourceLocation.fromNamespaceAndPath(MODID, "toggled");
		for (Item toggleableItem : InnatePropertyItem.TOGGLEABLE_ITEMS)
			ItemProperties.register(toggleableItem, toggled, ((stack, level, entity, seed) ->
					AlchemancyProperties.TOGGLEABLE.get().getData(stack) ? 1 : 0));

		ItemProperties.register(AlchemancyItems.WAYWARD_MEDALLION.get(), ResourceLocation.fromNamespaceAndPath(MODID, "bound"),
				((stack, level, entity, seed) -> AlchemancyProperties.WAYWARD_WARP.value().getData(stack).hasTarget() ? 1 : 0));

		registerItemProperties("active", (stack, level, entity, seed) -> entity != null && stack.equals(entity.getUseItem()) ? 1 : 0,
				AlchemancyItems.ROCKET_POWERED_HAMMER, AlchemancyItems.BARRELS_WARHAMMER, AlchemancyItems.DREAMSTEEL_BOW);

		registerItemProperties("use_time", (stack, level, entity, seed) -> {
					if (entity == null) {
						return 0.0F;
					} else {
						return entity.getUseItem() != stack ? 0.0F : (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
					}
				},
				AlchemancyItems.DREAMSTEEL_BOW);
	}

	private static void registerItemProperties(String key, ItemPropertyFunction function, DeferredItem<?>... items)
	{
		ResourceLocation location = ResourceLocation.fromNamespaceAndPath(MODID, key);
		for (DeferredItem<?> item : items) {
			ItemProperties.register(item.asItem(), location, function);
		}
	}

	@SubscribeEvent
	public static void initItemColors(RegisterColorHandlersEvent.Item event)
	{
		event.register(((stack, tintIndex) ->
		{
			int[] colors = stack.getOrDefault(AlchemancyItems.Components.STORED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties().stream().map(propertyHolder -> propertyHolder.value().getColor(stack)).mapToInt(Integer::intValue).toArray();
			return colors.length == 0 ? -1 : FastColor.ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1, 2, colors));
		}), AlchemancyItems.PROPERTY_CAPSULE);

		event.register(((stack, tintIndex) -> {
			if(tintIndex != 1)
				return -1;
			int[] colors = stack.getOrDefault(AlchemancyItems.Components.INFUSED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties().stream()
					.filter(p -> !p.is(AlchemancyTags.Properties.SLOTLESS)).map(propertyHolder -> propertyHolder.value().getColor(stack)).mapToInt(Integer::intValue).toArray();
			return colors.length == 0 ? -1 : FastColor.ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1, 2, colors));
		}), AlchemancyItems.IRON_RING);

		event.register(((stack, tintIndex) -> {
			if(tintIndex != 1)
				return -1;
			int[] colors = stack.getOrDefault(AlchemancyItems.Components.INFUSED_PROPERTIES, InfusedPropertiesComponent.EMPTY).properties()
					.stream().filter(p -> !p.is(AlchemancyTags.Properties.IGNORED_BY_INFUSION_FLASK)).map(propertyHolder -> propertyHolder.value().getColor(stack)).mapToInt(Integer::intValue).toArray();
			return colors.length == 0 ? -1 : FastColor.ARGB32.color(255, ColorUtils.interpolateColorsAndWait(1, 2, colors));
		}), AlchemancyItems.INFUSION_FLASK);

		event.register(((stack, tintIndex) -> tintIndex == 1 ? AlchemancyProperties.TINTED_LENS.value().getColor(stack) : -1),
				AlchemancyItems.TINTED_GLASSES);

		event.register(((stack, tintIndex) -> tintIndex == 1 ? AlchemancyProperties.AWAKENED.value().getColor(stack) : -1),
				AlchemancyItems.PROPERTY_VISOR,
				AlchemancyItems.DREAMSTEEL_INGOT,
				AlchemancyItems.DREAMSTEEL_NUGGET,
				AlchemancyItems.DREAMSTEEL_PICKAXE,
				AlchemancyItems.DREAMSTEEL_AXE,
				AlchemancyItems.DREAMSTEEL_SHOVEL,
				AlchemancyItems.DREAMSTEEL_HOE,
				AlchemancyItems.DREAMSTEEL_SWORD,
				AlchemancyItems.DREAMSTEEL_BOW
		);

		event.register(((stack, tintIndex) -> tintIndex == 0 ? AlchemancyProperties.WAYWARD_WARP.value().getColor(stack) : -1),
				AlchemancyItems.WAYWARD_MEDALLION);

		event.register(((stack, tintIndex) -> tintIndex == 0 ? AlchemancyProperties.PARADOXICAL.value().getColor(stack) : -1),
				AlchemancyItems.PARADOX_PEARL);
	}

	@SubscribeEvent
	public static void initClientExtensions(RegisterClientExtensionsEvent event)
	{
		event.registerItem(new IClientItemExtensions() {
			@Override
			public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return AlchemancyCatalystItemRenderer.instance;
			}
		}, AlchemancyItems.ALCHEMANCY_CATALYST.get());
	}

	@SubscribeEvent
	public static void initAdditionalModels(ModelEvent.RegisterAdditional event)
	{
		event.register(AlchemancyCatalystItemRenderer.FRAME_LOCATION);
	}

	@SubscribeEvent
	public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(CodexEntryReloadListenener.INSTANCE);
	}
}
