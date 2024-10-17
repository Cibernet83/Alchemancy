package net.cibernet.alchemancy.properties;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.TriState;

public class LightweightProperty extends Property
{
	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		if (!entity.isNoGravity())
		{
			entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
			entity.hasImpulse = true;
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile entity) {
		if (!entity.isNoGravity())
		{
			entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
			entity.hasImpulse = true;
		}
	}

	@Override
	public TriState isItemInTag(ItemStack stack, TagKey<Item> tagKey)
	{
		return tagKey == ItemTags.ARMADILLO_FOOD ? TriState.TRUE : TriState.DEFAULT;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xD0D6E5;
	}
}
