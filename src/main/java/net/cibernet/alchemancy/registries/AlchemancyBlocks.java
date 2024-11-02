package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.*;
import net.cibernet.alchemancy.blocks.blockentities.SculkBudBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyBlocks
{
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Alchemancy.MODID);


	public static final DeferredBlock<FlowerBlock> BLAZEBLOOM = REGISTRY.register("blazebloom", () -> new BlazebloomBlock(MobEffects.FIRE_RESISTANCE, 10, BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT)
			.noCollission()
			.instabreak()
			.sound(SoundType.GRASS)
			.offsetType(BlockBehaviour.OffsetType.XZ)
			.lightLevel(state -> 3)
			.pushReaction(PushReaction.DESTROY)){

	});

	public static final DeferredBlock<PottedBlazebloomBlock> POTTED_BLAZEBLOOM = REGISTRY.register("potted_blazebloom", () -> new PottedBlazebloomBlock(BLAZEBLOOM, BlockBehaviour.Properties.of()
			.instabreak()
			.noOcclusion()
			.lightLevel(state -> 3)
			.pushReaction(PushReaction.DESTROY)));

	public static final DeferredBlock<EssenceExtractorBlock> ESSENCE_EXTRACTOR = REGISTRY.register("essence_extractor", () -> new EssenceExtractorBlock(BlockBehaviour.Properties.of().strength(1.5f)));
	public static final DeferredBlock<EssenceInjectorBlock> ESSENCE_INJECTOR = REGISTRY.register("essence_injector", () -> new EssenceInjectorBlock(BlockBehaviour.Properties.of().strength(1.5f)));
	public static final DeferredBlock<InfusionPedestalBlock> INFUSION_PEDESTAL = REGISTRY.register("infusion_pedestal", () -> new InfusionPedestalBlock(BlockBehaviour.Properties.of().strength(1.5f)));
	public static final DeferredBlock<AlchemancyForgeBlock> ALCHEMANCY_FORGE = REGISTRY.register("alchemancy_forge", () -> new AlchemancyForgeBlock(BlockBehaviour.Properties.of().strength(1.5f)));
	public static final DeferredBlock<AlchemancyCatalystBlock> ALCHEMANCY_CATALYST = REGISTRY.register("alchemancy_catalyst", () -> new AlchemancyCatalystBlock(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.GLASS).noOcclusion()));
	public static final DeferredBlock<RootedItemBlock> ROOTED_ITEM = REGISTRY.register("rooted_item", () -> new RootedItemBlock(BlockBehaviour.Properties.of().sound(SoundType.CROP).noOcclusion().mapColor(MapColor.PLANT)));
	public static final DeferredBlock<FlattenedItemBlock> FLATTENED_ITEM = REGISTRY.register("flattened_item", () -> new FlattenedItemBlock(BlockBehaviour.Properties.of().noOcclusion()));
	public static final DeferredBlock<SculkBudBlock> SCULK_BUD = REGISTRY.register("sculk_bud", () -> new SculkBudBlock(BlockBehaviour.Properties.of().sound(SoundType.SCULK).mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(SoundType.SCULK)));

	public static final DeferredBlock<GlowingOrbBlock> GLOWING_ORB = REGISTRY.register("glowing_orb", () -> new GlowingOrbBlock(
			BlockBehaviour.Properties.of()
					.noCollission()
					.instabreak()
					.pushReaction(PushReaction.DESTROY)
					.replaceable()
					.lightLevel(state -> 15)));

}
