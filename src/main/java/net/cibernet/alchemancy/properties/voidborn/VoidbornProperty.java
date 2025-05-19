package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.properties.AbstractTimerProperty;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class VoidbornProperty extends AbstractTimerProperty {

	public static final int MAX_TIME = 200;

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		tickEntity(stack, itemEntity);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		tickEntity(stack, projectile);
	}

	private void tickEntity(ItemStack stack, Entity entity) {

		long elapsedTime = getElapsedTime(stack);
		if(hasRecordedTimestamp(stack) && elapsedTime < MAX_TIME)
		{
			entity.setGlowingTag(true);
			entity.setDeltaMovement(entity.getDeltaMovement().scale(0.45f).add(0.0D, 0.1 + 0.5f * (1 - getElapsedTime(stack) / (float)MAX_TIME), 0.0D));
		}
		else entity.setGlowingTag(false);
	}

	@Override
	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
		removeData(stack);
	}

	@Override
	public boolean onEntityItemBelowWorld(ItemStack stack, ItemEntity itemEntity) {
		resetStartTimestamp(stack);
		return true;
	}

	public static boolean isBelowWorld(Entity entity) {
		return (entity.getY() < (double)(entity.level().getMinBuildHeight() - 64));
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(1f, 0x1B0C1B, 0x280099, 0x7100A5, 0x1B0C1B, 0x280099, 0x280099, 0x1B0C1B, 0x1B0C1B, 0x7100A5, 0x7100A5, 0x7100A5, 0x280099, 0x1B0C1B, 0x1B0C1B);
	}
}
