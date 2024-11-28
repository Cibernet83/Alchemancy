package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
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

		if(target.level() instanceof ServerLevel serverLevel && PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
		{
			if(user == null)
				weapon.hurtAndBreak(20, serverLevel, user, (item) -> {});
			else weapon.hurtAndBreak(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20), user, EquipmentSlot.MAINHAND);
		}
		else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getColor(ItemStack stack) {
		return colors[(int) Math.abs((System.currentTimeMillis() / 10) % colors.length)];
	}
}
