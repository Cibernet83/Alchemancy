package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LightningBoltProperty extends Property
{
	private final int[] colors = new int[]{0xB5FFFF, 0x1FB2FF};

	@Override
	public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) 
	{
		LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(target.level());
		lightningbolt.moveTo(target.position());
		lightningbolt.setCause(user instanceof ServerPlayer ? (ServerPlayer)user : null);
		target.level().addFreshEntity(lightningbolt);

		if(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
			weapon.hurtAndBreak(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20), user, EquipmentSlot.MAINHAND);
		else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getColor(ItemStack stack) {
		return colors[(int) Math.abs((System.currentTimeMillis() / 10) % colors.length)];
	}
}
