package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.advancements.critereon.MovementPredicate;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class DecayingProperty extends MobEffectOnHitProperty
{
	public DecayingProperty()
	{
		super(new MobEffectInstance(MobEffects.WITHER, 100, 1));
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(!user.level().isClientSide && user.tickCount % 20 == 0)
			stack.hurtAndBreak(PropertyModifierComponent.get(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION), user, slot);
	}
}
