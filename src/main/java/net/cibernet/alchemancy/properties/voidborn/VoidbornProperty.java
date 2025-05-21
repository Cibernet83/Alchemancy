package net.cibernet.alchemancy.properties.voidborn;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.AbstractTimerProperty;
import net.cibernet.alchemancy.properties.special.RocketPoweredProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class VoidbornProperty extends AbstractTimerProperty {

	public static final int MAX_TIME = 200;

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {
		tickEntity(stack, itemEntity);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {
		tickEntity(stack, projectile);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		if (isBelowWorld(user))
		{
			resetStartTimestamp(stack);
			if(stack.isDamageableItem())
				stack.hurtAndBreak(20, user, slot);
			else consumeItem(user, stack, slot);

		}
		tickEntity(stack, user);
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY)) {
			if (!PropertyModifierComponent.getOrElse(stack, AlchemancyProperties.AUXILIARY, AlchemancyProperties.Modifiers.IGNORE_INFUSED, false) ||
					InfusedPropertiesHelper.hasInnateProperty(stack, asHolder())) {
				if (isBelowWorld(user))
				{
					resetStartTimestamp(stack);
					if(stack.isDamageableItem() && user instanceof LivingEntity living)
						stack.hurtAndBreak(20, living, EquipmentSlot.CHEST);
					else consumeItem(user, stack, EquipmentSlot.CHEST);
				}
				tickEntity(stack, user);
			}
		}
	}

	private void tickEntity(ItemStack stack, Entity entity) {

		long elapsedTime = getElapsedTime(stack);
		if (hasRecordedTimestamp(stack) && elapsedTime < MAX_TIME && !entity.isShiftKeyDown()) {
			float hScale = 0.45f;
			if (entity instanceof LivingEntity living) {
				hScale = 1;
				living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false));
			} else entity.setGlowingTag(true);
			entity.setDeltaMovement(entity.getDeltaMovement().multiply(hScale, 0.45f, hScale).add(0.0D, 0.1 + 0.5f * (1 - getElapsedTime(stack) / (float) MAX_TIME), 0.0D));
		} else {
			if (hasRecordedTimestamp(stack)) {
				removeData(stack);
				if (!(entity instanceof LivingEntity))
					entity.setGlowingTag(false);
			}
		}
	}

	@Override
	public void onItemPickedUp(Player player, ItemStack stack, ItemEntity itemEntity) {
		removeData(stack);
	}

	@Override
	public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD))
			event.setCanceled(true);
	}

	@Override
	public boolean onEntityItemBelowWorld(ItemStack stack, ItemEntity itemEntity) {
		resetStartTimestamp(stack);
		return true;
	}

	public static boolean isBelowWorld(Entity entity) {
		return (entity.getY() < (double) (entity.level().getMinBuildHeight() - 64));
	}

	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(1f, 0x1B0C1B, 0x280099, 0x7100A5, 0x1B0C1B, 0x280099, 0x280099, 0x1B0C1B, 0x1B0C1B, 0x7100A5, 0x7100A5, 0x7100A5, 0x280099, 0x1B0C1B, 0x1B0C1B);
	}
}
