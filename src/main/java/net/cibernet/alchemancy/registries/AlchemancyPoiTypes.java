package net.cibernet.alchemancy.registries;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import net.caffeinemc.mods.sodium.client.util.ListUtil;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlchemancyPoiTypes {

	public static final DeferredRegister<PoiType> REGISTRY = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<PoiType, PoiType> TICKING_BLOCK = REGISTRY.register("ticking_block", () -> new PoiType(
			Streams.concat(
					getBlockStates(AlchemancyBlocks.GUST_BASKET.get()).stream(),
					getBlockStates(AlchemancyBlocks.FLAT_HOPPER.get()).stream()
					).collect(Collectors.toSet()), 0, 1
	));
	public static final DeferredHolder<PoiType, PoiType> ROOTED_ITEM = REGISTRY.register("rooted_item", () -> new PoiType(
			getBlockStates(AlchemancyBlocks.ROOTED_ITEM.get()), 0, 1
	));

	private static Set<BlockState> getBlockStates(Block block) {

		return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
	}
}
