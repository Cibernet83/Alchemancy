package net.cibernet.alchemancy;

import com.mojang.logging.LogUtils;
import net.cibernet.alchemancy.commands.AlchemancyCommands;
import net.cibernet.alchemancy.registries.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

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
		AlchemancyCriteriaTriggers.REGISTRY.register(modEventBus);
		AlchemancySoundEvents.REGISTRY.register(modEventBus);
		AlchemancyPoiTypes.REGISTRY.register(modEventBus);
		AlchemancyDataAttachments.REGISTRY.register(modEventBus);
		AlchemancyCommands.Arguments.REGISTRY.register(modEventBus);

		// Register our mod's ModConfigSpec so that FML can create and load the config file for us
		//modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event)
	{
	}

}
