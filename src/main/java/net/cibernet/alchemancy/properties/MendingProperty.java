package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MendingProperty extends Property
{

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(event.isCanceled())
			target.heal(event.getAmount());
	}

	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		event.getEntity().heal(event.getNewDamage());
		event.setNewDamage(0);
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		if(target instanceof LivingEntity living)
			living.heal(Mth.ceil(getItemAttackDamage(stack) * 0.65f));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(user.level().isClientSide || !slot.isArmor())
			return;

		if(stack.isDamageableItem())
		{
			if(user.tickCount % 100 == 0 && user.getHealth() < user.getMaxHealth())
			{
				stack.hurtAndBreak(1, user, slot);
				user.heal(1);
			}
		}
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack stack, EquipmentSlot slot, DamageSource damageSource)
	{
		if (slot.isArmor() && entityLowOnHealth(user))
		{
			user.onEquippedItemBroken(stack.getItem(), slot);
			Item item = stack.getItem();
			stack.shrink(1);
			if (user instanceof Player player)
				player.awardStat(Stats.ITEM_BROKEN.get(item));
			stack.setDamageValue(0);

			user.heal(user.getMaxHealth() * 0.4f);
		}
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		if(root.getTickCount() % 60 == 0)
			for (LivingEntity entity : entitiesInBounds) {
				entity.heal(2);
			}
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.HAPPY_VILLAGER);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF7098;
	}

	@Override
	public int getPriority() {
		return Priority.LOWER;
	}

}
