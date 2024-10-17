package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ShockUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShockDamageProperty extends Property
{
	public final float power(ItemStack stack)
	{
		return PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.ATTACK_DAMAGE, 5f);
	}
	
	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		if(user == null)
			ShockUtils.environmentalShockAttack(target.level(), target.position(), power(weapon));
		else if(user.equals(damageSource.getEntity()))
			ShockUtils.meleeShockAttack(user, target, power(weapon));
		else ShockUtils.rangedShockAttack(damageSource.getEntity(), user, target, power(weapon));
	}

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource)
	{
		if(source == null)
			ShockUtils.environmentalShockAttack(target.level(), target.position(), power(stack));
		else ShockUtils.selfDamagingMeleeShockAttack(source, target.position(), power(stack));
	}

	@Override
	public void onRootedTick(RootedItemBlockEntity root, List<LivingEntity> entitiesInBounds)
	{
		if(root.getLevel().getRandom().nextFloat() < 0.05f)
			ShockUtils.environmentalShockAttack(root.getLevel(), root.getBlockPos().getCenter(), power(root.getItem()));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x73e5ff;
	}
}
