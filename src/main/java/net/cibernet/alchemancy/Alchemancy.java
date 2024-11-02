package net.cibernet.alchemancy;

import com.mojang.logging.LogUtils;
import net.cibernet.alchemancy.client.render.AlchemancyCatalystRenderer;
import net.cibernet.alchemancy.client.render.ItemStackHolderRenderer;
import net.cibernet.alchemancy.client.render.RootedItemRenderer;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.registries.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Alchemancy.MODID)
public class Alchemancy {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "alchemancy";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();



	// The constructor for the mod class is the first code that is run when your mod is loaded.
	// FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
	public Alchemancy(IEventBus modEventBus, ModContainer modContainer)
	{
		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		//Register Registries
		AlchemancyBlocks.REGISTRY.register(modEventBus);
		AlchemancyBlockEntities.REGISTRY.register(modEventBus);
		AlchemancyItems.REGISTRY.register(modEventBus);
		AlchemancyItems.Materials.ARMOR_MATERIAL_REGISTRY.register(modEventBus);
		AlchemancyItems.Components.REGISTRY.register(modEventBus);
		AlchemancyEntities.REGISTRY.register(modEventBus);
		AlchemancyEssence.REGISTRY.register(modEventBus);
		AlchemancyProperties.REGISTRY.register(modEventBus);
		AlchemancyProperties.Modifiers.REGISTRY.register(modEventBus);
		AlchemancyCreativeTabs.REGISTRY.register(modEventBus);
		AlchemancyRecipeTypes.REGISTRY.register(modEventBus);
		AlchemancyRecipeTypes.Serializers.REGISTRY.register(modEventBus);
		AlchemancyParticles.REGISTRY.register(modEventBus);
		AlchemancyWorldGen.Features.REGISTRY.register(modEventBus);
		AlchemancyIngredients.REGISTRY.register(modEventBus);

		// Register our mod's ModConfigSpec so that FML can create and load the config file for us
		//modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event)
	{
	}

}
