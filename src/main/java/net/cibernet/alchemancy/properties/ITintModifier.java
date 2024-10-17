package net.cibernet.alchemancy.properties;

import net.minecraft.world.item.ItemStack;

public interface ITintModifier
{
	int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint);

	default boolean modifiesAlpha()
	{
		return false;
	}
}
