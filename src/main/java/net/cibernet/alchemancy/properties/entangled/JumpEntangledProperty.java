package net.cibernet.alchemancy.properties.entangled;

import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.mixin.accessors.LivingEntityAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class JumpEntangledProperty extends AbstractEntangledProperty
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(!(user instanceof Player player))
			return;

		boolean jumping = ((LivingEntityAccessor) user).isJumping();
		if (user.level().isClientSide() && user instanceof LocalPlayer localPlayer) //Dumbest way to check for jump input serverside
			localPlayer.connection.send(new ServerboundPlayerInputPacket(localPlayer.xxa, localPlayer.zza, jumping, localPlayer.isShiftKeyDown()));

		if(jumping != getToggle(stack) && player.getInventory().getItem(inventorySlot) == stack)
		{
			setToggle(stack, jumping);
			player.getInventory().setItem(inventorySlot, shift(stack, player));
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		Entity user = itemEntity.getOwner();
		if(itemEntity.getItem() == stack && user instanceof LivingEntity living)
		{
			boolean jumping = ((LivingEntityAccessor) living).isJumping();
			if(jumping != getToggle(stack))
			{
				setToggle(stack, jumping);
				itemEntity.setItem(shift(stack, living));
				afterShiftingProjectile(stack, itemEntity.getItem(), itemEntity);
			}
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile)
	{
		if(projectile instanceof InfusedItemProjectile infusedItemProjectile && infusedItemProjectile.getItem() == stack)
		{
			Entity user = projectile.getOwner();
			if(user instanceof LivingEntity living)
			{
				boolean jumping = ((LivingEntityAccessor) living).isJumping();
				if(jumping != getToggle(stack))
				{
					setToggle(stack, jumping);
					infusedItemProjectile.setItem(shift(stack, living));
					afterShiftingProjectile(stack, infusedItemProjectile.getItem(), infusedItemProjectile);
				}
			}
		}
	}
}
