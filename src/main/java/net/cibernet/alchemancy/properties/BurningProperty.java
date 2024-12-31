package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

public class BurningProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xFF4E00;
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), (int) (100 * getBoostFromEnchantments(user.level(), weapon))));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(slot.isArmor() && user.tickCount % 10 == 0)
			user.setRemainingFireTicks(Math.max(user.getRemainingFireTicks(), 20));
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		for (LivingEntity entity : entitiesInBounds) {
			entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), (int) (80 * getBoostFromEnchantments(root.getLevel(), root.getItem()))));
		}
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setRemainingFireTicks(80);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.FLAME);
	}

	protected static float getBoostFromEnchantments(Level level, ItemStack stack)
	{
		float result = 1;
		HolderLookup.RegistryLookup<Enchantment> lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		ItemEnchantments enchantments = stack.getAllEnchantments(lookup);
		for (Holder<Enchantment> enchantment : lookup.getOrThrow(AlchemancyTags.Enchantments.BUFFS_BURNING)) {
			result += enchantments.getLevel(enchantment) * 1f;
		}

		return result;
	}
}
