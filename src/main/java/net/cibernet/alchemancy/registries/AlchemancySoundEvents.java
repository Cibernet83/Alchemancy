package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancySoundEvents
{
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, Alchemancy.MODID);

	public static final Holder<SoundEvent> ALCHEMANCY_CRYSTAL_ACTIVATE = register("block.alchemancy_catalyst.activate");
	public static final Holder<SoundEvent> ALCHEMANCY_CRYSTAL_FIRE = register("block.alchemancy_catalyst.use");
	public static final Holder<SoundEvent> GLOWING_ORB_EXTINGUISH = register("block.glowing_orb.extinguish");
	public static final Holder<SoundEvent> GUST_BASKET = register("block.gust_basket.blow");
	public static final Holder<SoundEvent> PHANTOM_MEMBRANE_POP = register("block.phantom_membrane_block.pop");

	public static final Holder<SoundEvent> HEAVY = register("property.heavy");
	public static final Holder<SoundEvent> BOUNCY = register("property.bouncy.jump");
	public static final Holder<SoundEvent> BOUNCY_SMALL = register("property.bouncy.jump_small");
	public static final Holder<SoundEvent> TOGGLEABLE = register("property.toggleable");
	public static final Holder<SoundEvent> TICKING = register("property.ticking");
	public static final Holder<SoundEvent> SMELTING_RECHARGE = register("property.smelting.recharge");
	public static final Holder<SoundEvent> SMELTING_DEPLETED = register("property.smelting.depleted");
	public static final Holder<SoundEvent> CALCAREOUS_MILK = register("property.calcareous.milk");
	public static final Holder<SoundEvent> HOLLOW_DROP_CONTENTS = register("property.hollow.drop_contents");
	public static final Holder<SoundEvent> HOLLOW_INSERT = register("property.hollow.insert");
	public static final Holder<SoundEvent> THROWABLE = register("property.throwable");
	public static final Holder<SoundEvent> LOYAL = register("property.loyal");
	public static final Holder<SoundEvent> CLUELESS = register("property.clueless");
	public static final Holder<SoundEvent> WAYFINDING = register("property.wayfinding");
	public static final Holder<SoundEvent> ALLERGIC = register("property.allergic");
	public static final Holder<SoundEvent> CLAY_MOLD = register("property.clay_mold");
	public static final Holder<SoundEvent> HOME_RUN_HIT = register("property.home_run.hit");
	public static final Holder<SoundEvent> HOME_RUN_FAIL = register("property.home_run.fail");
	public static final Holder<SoundEvent> GUST_JET = register("property.gust_jet");


	public static final Holder<SoundEvent> BADA_QUIP = register("property.bada_quip");


	public static final SoundType GLOWING_ORB = new DeferredSoundType(
			0.1f,
			0.5f,
			GLOWING_ORB_EXTINGUISH::value,
			() -> SoundEvents.GLASS_STEP,
			() -> SoundEvents.GLASS_PLACE,
			() -> SoundEvents.GLASS_HIT,
			() -> SoundEvents.GLASS_FALL
			);

	private static Holder<SoundEvent> register(String key)
	{
		return REGISTRY.register(key, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, key)));
	}
}
