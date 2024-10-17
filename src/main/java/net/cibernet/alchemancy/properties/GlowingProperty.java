package net.cibernet.alchemancy.properties;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class GlowingProperty extends MobEffectEquippedAndHitProperty
{
	public GlowingProperty() {
		super(new MobEffectInstance(MobEffects.GLOWING, 600, 0, false, false), EquipmentSlotGroup.ANY, false);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setGlowingTag(true);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity)
	{
		itemEntity.setGlowingTag(true);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFDF55F;
	}
}
