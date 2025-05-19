package net.cibernet.alchemancy.item;

import net.cibernet.alchemancy.entity.InfusionFlask;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class InfusionFlaskItem extends Item implements ProjectileItem {

	public InfusionFlaskItem(Item.Properties properties) {
		super(properties);
		DispenserBlock.registerProjectileBehavior(this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			InfusionFlask flask = new InfusionFlask(level, player);
			flask.setItem(itemstack);
			flask.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
			level.addFreshEntity(flask);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		itemstack.consume(1, player);
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		InfusionFlask flask = new InfusionFlask(level, pos.x(), pos.y(), pos.z());
		flask.setItem(stack);
		return flask;
	}

	@Override
	public ProjectileItem.DispenseConfig createDispenseConfig() {
		return ProjectileItem.DispenseConfig.builder()
				.uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
				.power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
				.build();
	}
}
