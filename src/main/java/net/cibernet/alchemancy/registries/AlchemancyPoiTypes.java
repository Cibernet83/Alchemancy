package net.cibernet.alchemancy.registries;

import com.google.common.collect.ImmutableSet;
import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class AlchemancyPoiTypes {

	public static final DeferredRegister<PoiType> REGISTRY = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Alchemancy.MODID);

	public static final DeferredHolder<PoiType, PoiType> GUST_BASKET = REGISTRY.register("gust_basket", () -> new PoiType(
			getBlockStates(AlchemancyBlocks.GUST_BASKET.get()), 0, 1
	));

	private static Set<BlockState> getBlockStates(Block block) {
		return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
	}
}
