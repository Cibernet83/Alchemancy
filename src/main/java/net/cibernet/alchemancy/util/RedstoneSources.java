package net.cibernet.alchemancy.util;

import net.cibernet.alchemancy.data.save.AlchemancyLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;

@EventBusSubscriber
public class RedstoneSources
{
	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Post event)
	{
		if(event.getLevel() instanceof ServerLevel serverLevel)
			tick(serverLevel);
	}

	public static void createSourceAt(ServerLevel level, BlockPos pos, int power, int ticks, Direction direction)
	{
		if(level.getBlockState(pos).is(BlockTags.AIR))
			return;

		AlchemancyLevelData data = AlchemancyLevelData.compute(level);
		data.getRedstoneSources().put(pos, new RedstoneSource(power, ticks, direction));
		data.setDirty();
		updateBlock(level, pos);

	}

	public static int getSourcePower(ServerLevel level, BlockPos pos)
	{
		return AlchemancyLevelData.compute(level).getRedstoneSources().getOrDefault(pos, RedstoneSource.DEFAULT).power;
	}

	public static RedstoneSource getSourceAt(ServerLevel level, BlockPos pos)
	{
		return AlchemancyLevelData.compute(level).getRedstoneSources().getOrDefault(pos, RedstoneSource.DEFAULT);
	}

	public static void tick(ServerLevel level)
	{
		ArrayList<BlockPos> updates = new ArrayList<>();

		AlchemancyLevelData data = AlchemancyLevelData.compute(level);
		data.getRedstoneSources().entrySet().removeIf(entry ->
		{
			if(entry.getValue().ticks-- < 0)
			{
				entry.getValue().power = 0;
				updates.add(entry.getKey());
				data.setDirty();
				return true;
			}
			return false;
		});

		if(!updates.isEmpty())
			updates.forEach(pos -> updateBlock(level, pos));
	}
	private static void updateBlock(ServerLevel level, BlockPos pos) {
		level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
		level.neighborChanged(pos, Blocks.AIR, pos.above());
	}

	public static class RedstoneSource
	{
		public static final RedstoneSource DEFAULT = new RedstoneSource(0, 0, Direction.UP);

		public int power;
		public int ticks;
		public final Direction direction;

		public RedstoneSource(int power, int ticks, Direction direction) {
			this.power = power;
			this.ticks = ticks;
			this.direction = direction;
		}

		public CompoundTag write()
		{
			CompoundTag tag = new CompoundTag();
			tag.putInt("power", power);
			tag.putInt("ticks", ticks);
			tag.putString("direction", direction.getName());
			return tag;
		}

		public static RedstoneSource read(CompoundTag tag)
		{
			return new RedstoneSource(tag.getInt("power"), tag.getInt("ticks"), Direction.byName(tag.getString("direction")));
		}
	}
}
