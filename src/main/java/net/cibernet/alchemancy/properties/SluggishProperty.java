package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class SluggishProperty extends MobEffectOnHitProperty
{
	private static final AttributeModifier SPEED_MOD = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "sluggish_property_modifier"), -0.55F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	public SluggishProperty() {
		super(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
	}

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		if(getEquipmentSlotForItem(event.getItemStack()) != EquipmentSlot.MAINHAND)
			event.addModifier(Attributes.MOVEMENT_SPEED, SPEED_MOD, EquipmentSlotGroup.ARMOR);
		event.addModifier(Attributes.ATTACK_SPEED, SPEED_MOD, EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return result * 2;
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		projectile.setDeltaMovement(projectile.getDeltaMovement().scale(0.85f));
	}
}
