package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.advancements.criterion.ActivateForgeTrigger;
import net.cibernet.alchemancy.advancements.criterion.DiscoverPropertyTrigger;
import net.cibernet.alchemancy.advancements.criterion.PerformForgeRecipeTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.cibernet.alchemancy.Alchemancy.MODID;

public class AlchemancyCriteriaTriggers
{
	public static final DeferredRegister<CriterionTrigger<?>> REGISTRY = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);

	public static final DeferredHolder<CriterionTrigger<?>, DiscoverPropertyTrigger> DISCOVER_PROPERTY = REGISTRY.register("discover_property", DiscoverPropertyTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, PerformForgeRecipeTrigger> PERFORM_FORGE_RECIPE = REGISTRY.register("perform_forge_recipe", PerformForgeRecipeTrigger::new);
	public static final DeferredHolder<CriterionTrigger<?>, ActivateForgeTrigger> ACTIVATE_FORGE = REGISTRY.register("activate_alchemancy_forge", ActivateForgeTrigger::new);
}
