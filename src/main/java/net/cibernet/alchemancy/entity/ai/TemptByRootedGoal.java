package net.cibernet.alchemancy.entity.ai;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.function.Predicate;

public class TemptByRootedGoal extends MoveToBlockGoal
{
	private final Predicate<ItemStack> items;

	public TemptByRootedGoal(PathfinderMob mob, double speedModifier, Predicate<ItemStack> items) {
		super(mob, speedModifier, 24);
		this.items = items;
	}

	public TemptByRootedGoal(PathfinderMob mob, double speedModifier, Holder<Property> property)
	{
		this(mob, speedModifier, stack -> InfusedPropertiesHelper.hasProperty(stack, property));
	}

	@Override
	protected boolean isValidTarget(LevelReader level, BlockPos pos) {
		ChunkAccess chunkaccess = level.getChunk(
				SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false
		);
		return chunkaccess != null
				&& chunkaccess.getBlockEntity(pos) instanceof RootedItemBlockEntity root
				&& items.test(root.getItem());
	}
}
