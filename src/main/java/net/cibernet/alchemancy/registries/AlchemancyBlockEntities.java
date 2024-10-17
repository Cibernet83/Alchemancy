package net.cibernet.alchemancy.registries;

import com.google.gson.internal.ConstructorConstructor;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.blocks.blockentities.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AlchemancyBlockEntities
{
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY =  DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EssenceExtractorBlockEntity>> ESSENCE_EXTRACTOR = REGISTRY.register(
			"essence_extractor", () -> BlockEntityType.Builder.of(EssenceExtractorBlockEntity::new, AlchemancyBlocks.ESSENCE_EXTRACTOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EssenceInjectorBlockEntity>> ESSENCE_INJECTOR = REGISTRY.register(
			"essence_injector", () -> BlockEntityType.Builder.of(EssenceInjectorBlockEntity::new, AlchemancyBlocks.ESSENCE_INJECTOR.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemStackHolderBlockEntity>> ITEMSTACK_HOLDER = REGISTRY.register(
			"pedestal", () -> BlockEntityType.Builder.of(ItemStackHolderBlockEntity::new, AlchemancyBlocks.INFUSION_PEDESTAL.get(), AlchemancyBlocks.ALCHEMANCY_FORGE.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemancyCatalystBlockEntity>> ALCHEMANCY_CATALYST = REGISTRY.register(
			"alchemancy_catalyst", () -> BlockEntityType.Builder.of(AlchemancyCatalystBlockEntity::new, AlchemancyBlocks.ALCHEMANCY_CATALYST.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RootedItemBlockEntity>> ROOTED_ITEM = REGISTRY.register(
			"rooted_item", () -> BlockEntityType.Builder.of(RootedItemBlockEntity::new, AlchemancyBlocks.ROOTED_ITEM.get()).build(null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkBudBlockEntity>> SCULK_BUD = REGISTRY.register(
			"sculk_bud", () -> BlockEntityType.Builder.of(SculkBudBlockEntity::new, AlchemancyBlocks.SCULK_BUD.get()).build(null));
}
