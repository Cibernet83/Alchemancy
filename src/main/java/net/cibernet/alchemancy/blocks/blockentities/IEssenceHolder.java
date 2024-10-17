package net.cibernet.alchemancy.blocks.blockentities;

import net.minecraft.core.Direction;

public interface IEssenceHolder
{
	EssenceContainer getEssenceContainer();

	default boolean canTransferFromDirection(Direction direction)
	{
		return true;
	}
}
