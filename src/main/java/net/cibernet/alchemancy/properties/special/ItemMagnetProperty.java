package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemMagnetProperty extends Property {
	private static final double RADIUS = 5;

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (user.tickCount % 10 != 0)
			return;

		if (user instanceof Player player)
			for (ItemEntity itemEntity : user.level().getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().inflate(RADIUS), item -> !item.hasPickUpDelay()))
				itemEntity.playerTouch(player);
		else
			for (ItemEntity itemEntity : user.level().getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().inflate(RADIUS)))
				itemEntity.setPos(user.position());

	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		for (ItemEntity itemEntity : projectile.level().getEntitiesOfClass(ItemEntity.class, projectile.getBoundingBox().inflate(RADIUS))) {
			itemEntity.setPos(projectile.position());
		}
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity projectile) {
		for (ItemEntity itemEntity : projectile.level().getEntitiesOfClass(ItemEntity.class, projectile.getBoundingBox().inflate(RADIUS))) {
			itemEntity.setPos(projectile.position());
		}
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds) {
		if (root.getTickCount() % 10 == 0)
			for (ItemEntity itemEntity : root.getLevel().getEntitiesOfClass(ItemEntity.class, CommonUtils.boundingBoxAroundPoint(root.getBlockPos().getCenter(), (float) RADIUS))) {
				itemEntity.setPos(root.getBlockPos().getCenter());
			}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xBE70FF;
	}
}
