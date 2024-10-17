package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;

public class WetProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0x1485FF;
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event) {

		if(slot.isArmor() && !weapon.is(AlchemancyTags.Items.INCREASES_SHOCK_DAMAGE_RECEIVED) && event.getSource().is(AlchemancyTags.DamageTypes.SHOCK_DAMAGE))
			event.setNewDamage(event.getNewDamage() + event.getOriginalDamage() * 0.1f);
	}

	@Override
	public void onAttack(Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(target.isOnFire())
			target.setRemainingFireTicks(target.getRemainingFireTicks() - 20);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(slot.isArmor() && user.tickCount % 5 == 0 && user.isOnFire())
			user.setRemainingFireTicks(user.getRemainingFireTicks() - 5);
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		root.setFarmlandWaterManager(FarmlandWaterManager.addAABBTicket(root.getLevel(), new AABB(root.getBlockPos()).inflate(4)));
		root.getLevel().getEntitiesOfClass(LivingEntity.class, root.getBlockState().getShape(root.getLevel(), root.getBlockPos()).bounds().move(root.getBlockPos())).forEach(
				entity -> entity.setRemainingFireTicks(0)
		);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource random)
	{
		playRootedParticles(root, random, ParticleTypes.DRIPPING_WATER);
	}
}
