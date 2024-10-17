package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class LevitatingProperty extends Property
{
	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity entity)
	{
		if (!entity.isNoGravity())
			entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.06, 0.0D));
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		if (!projectile.isNoGravity())
			projectile.setDeltaMovement(projectile.getDeltaMovement().add(0.0D, 0.06, 0.0D));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(!(user instanceof Player player && player.getAbilities().flying))
		{

			user.setDeltaMovement(new Vec3(user.getDeltaMovement().x, user.getDeltaMovement().y +
					PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.EFFECT_VALUE, 0.05f), user.getDeltaMovement().z));
			user.hasImpulse = true;
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return MobEffects.LEVITATION.value().getColor();
	}
}
