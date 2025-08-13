package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ParasiticProperty extends Property {

	static ResourceKey<DamageType> DAMAGE_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "parasitic"));

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(!user.level().isClientSide() && user.getRandom().nextFloat() < 0.01f && canRepair(stack, 10))
		{
			user.hurt(new DamageSource(user.damageSources().damageTypes.getHolderOrThrow(DAMAGE_KEY)), 1);
			repairItem(stack, 10);
		}
	}
	@Override
	public int getColor(ItemStack stack) {
		return 0xAA295C;
	}
}
