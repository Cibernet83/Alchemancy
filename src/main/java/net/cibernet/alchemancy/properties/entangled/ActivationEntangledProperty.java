package net.cibernet.alchemancy.properties.entangled;

import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ActivationEntangledProperty extends AbstractEntangledProperty
{
	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		switch (source)
		{
			case Player player ->
			{
				Inventory inventory = player.getInventory();
				for (int i = 0; i < inventory.getContainerSize(); i++)
				{
					ItemStack currentStack = inventory.getItem(i);
					if (stack == currentStack)
					{
						inventory.setItem(i, shift(stack));
						return;
					}
				}
			}
			case InfusedItemProjectile projectile ->
			{
				if(projectile.getItem() == stack)
					projectile.setItem(shift(stack));
			}
			case null, default -> {}
		}
	}

	@Override
	public void onActivationByBlock(Level level, BlockPos position, Entity target, ItemStack stack)
	{
		if(level.getBlockEntity(position) instanceof ItemStackHolderBlockEntity blockEntity)
			blockEntity.setItem(shift(stack));
	}
}
