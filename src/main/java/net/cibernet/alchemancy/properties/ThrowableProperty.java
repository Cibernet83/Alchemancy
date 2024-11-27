package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ThrowableProperty extends Property
{
	public static final float THROW_VELOCITY = 1.5f;

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(!event.isCanceled())
		{
			throwItem(event.getLevel(), event.getEntity(), event.getItemStack());
			event.setCancellationResult(InteractionResult.SUCCESS);
			event.setCanceled(true);
		}
	}

	private void throwItem(Level level, LivingEntity user, ItemStack stack)
	{
		if(stack.isEmpty())
			return;

		level.playSound(
				null,
				user.getX(),
				user.getY(),
				user.getZ(),
				SoundEvents.SNOWBALL_THROW,
				SoundSource.PLAYERS,
				0.5F,
				0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		if (!level.isClientSide) {
			boolean sharpshooting = InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHARPSHOOTING);
			InfusedItemProjectile thrownItem = new InfusedItemProjectile(user, level);
			thrownItem.setItem(stack);
			thrownItem.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, THROW_VELOCITY * (sharpshooting ? 1.5f : 1f), 1.0F);
			level.addFreshEntity(thrownItem);
		}

		if(user instanceof Player player)
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		stack.consume(1, user);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDFCE9B;
	}
}
