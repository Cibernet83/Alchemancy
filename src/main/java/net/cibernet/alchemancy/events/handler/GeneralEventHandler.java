package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.blocks.GustBasketBlock;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyPoiTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.StreamSupport;

@EventBusSubscriber
public class GeneralEventHandler
{
	private static final HashMap<Block, BiConsumer<ServerLevel, BlockPos>> TICKING_BLOCK_FUNCTIONS = new HashMap<>();

	public static void registerTickingBlockFunction(Block block, BiConsumer<ServerLevel, BlockPos> function) {
		TICKING_BLOCK_FUNCTIONS.put(block, function);
	}

	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Pre event)
	{
		Level level = event.getLevel();

		if(level.isClientSide())
			level.updateSkyBrightness(); //Updating sky brightness on the client so skylight-dependent operations (i.e. photosynthetic check) can work clientside
		else if (level instanceof ServerLevel serverLevel)
		{
			PoiManager poiManager = serverLevel.getPoiManager();
			List<LevelChunk> chunks = StreamSupport.stream(serverLevel.getChunkSource().chunkMap.getChunks().spliterator(), false).map(ChunkHolder::getTickingChunk)
					.filter(Objects::nonNull).toList();

			if(chunks.isEmpty()) return;

			RootedItemBlockEntity.cahceRootedItems(serverLevel, chunks);
			for (LevelChunk chunk : chunks) {
				poiManager.getInChunk(type -> type.equals(AlchemancyPoiTypes.TICKING_BLOCK), chunk.getPos(), PoiManager.Occupancy.ANY).map(PoiRecord::getPos).forEach(pos -> {

					Block block = serverLevel.getBlockState(pos).getBlock();

					if(TICKING_BLOCK_FUNCTIONS.containsKey(block))
						TICKING_BLOCK_FUNCTIONS.get(block).accept(serverLevel, pos);

					//GustBasketBlock.tick(serverLevel, pos);
				});
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		if(event.getEntity().level().isClientSide())
			GustBasketBlock.clientPlayerTick(event.getEntity());
	}
}
