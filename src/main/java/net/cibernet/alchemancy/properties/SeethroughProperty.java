package net.cibernet.alchemancy.properties;

import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

public class SeethroughProperty extends IncreaseInfuseSlotsProperty implements ITintModifier
{
	private static final int ALPHA = 120;
	private static final int DEFAULT_TINT = FastColor.ARGB32.color(ALPHA, 255, 255, 255);

	public SeethroughProperty() {
		super(1);
	}

	@Override
	public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint)
	{
		return currentTint == -1 ? DEFAULT_TINT : FastColor.ARGB32.color(ALPHA, currentTint);
	}

	@Override
	public boolean modifiesAlpha() {
		return true;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD0EAE9;
	}
}
