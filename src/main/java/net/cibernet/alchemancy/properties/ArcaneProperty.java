package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ArcaneProperty extends Property
{

	static ResourceKey<DamageType> MELEE_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "arcane"));
	static ResourceKey<DamageType> PROJECTILE_DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "arcane_projectile"));

	@Override
	public int getColor(ItemStack stack) {
		return 0xEA23C2;
	}

	@Override
	public int modifyEnchantmentValue(int originalValue, int result) {
		return result + 18;
	}

	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(!event.getSource().is(AlchemancyTags.DamageTypes.ARCANE_DAMAGE))
		{
			if(user instanceof LivingEntity living)
				weapon.hurtAndBreak(1, living, EquipmentSlot.MAINHAND);
			
			event.setCanceled(true);
			target.hurt(new DamageSource(
					target.damageSources().damageTypes.getHolderOrThrow(user.equals(event.getSource().getDirectEntity()) ? MELEE_DAMAGE_KEY : PROJECTILE_DAMAGE_KEY),
					event.getSource().getDirectEntity(), event.getSource().getEntity(), event.getSource().getSourcePosition()), event.getAmount());
		}
	}
}
