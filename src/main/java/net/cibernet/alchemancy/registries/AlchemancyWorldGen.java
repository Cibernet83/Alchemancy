package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.world.gen.feature.NearLavaVegetationFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.cibernet.alchemancy.Alchemancy.MODID;

public class AlchemancyWorldGen
{
	public static class Features
	{

		public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(Registries.FEATURE, MODID);

		public static final DeferredHolder<Feature<?>, NearLavaVegetationFeature> NEAR_LAVA_VEGETATION = REGISTRY.register("near_lava_vegetation", () -> new NearLavaVegetationFeature(NetherForestVegetationConfig.CODEC));

	}
}
