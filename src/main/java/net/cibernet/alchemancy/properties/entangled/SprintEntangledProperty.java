package net.cibernet.alchemancy.properties.entangled;

import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SprintEntangledProperty extends AbstractEntangledProperty
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(user instanceof Player player && user.isSprinting() != getToggle(stack) && player.getInventory().getItem(inventorySlot) == stack)
		{
			setToggle(stack, user.isSprinting());
			player.getInventory().setItem(inventorySlot, shift(stack, player));
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		Entity user = itemEntity.getOwner();
		if(itemEntity.getItem() == stack && user != null && user.isSprinting() != getToggle(stack))
		{
			setToggle(stack, user.isSprinting());
			itemEntity.setItem(shift(stack, user));
			afterShiftingProjectile(stack, itemEntity.getItem(), itemEntity);
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(projectile instanceof InfusedItemProjectile infusedItemProjectile && infusedItemProjectile.getItem() == stack)
		{
			Entity user = projectile.getOwner();
			if(user != null && user.isSprinting() != getToggle(stack))
			{
				setToggle(stack, user.isSprinting());
				infusedItemProjectile.setItem(shift(stack, user));
				afterShiftingProjectile(stack, infusedItemProjectile.getItem(), infusedItemProjectile);
			}
		}
	}
}
