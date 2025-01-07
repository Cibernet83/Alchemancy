package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@EventBusSubscriber
public class LightningBoltProperty extends Property
{

	@Override
	public void onCriticalAttack(@Nullable Player user, ItemStack weapon, Entity target) 
	{
		LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(target.level());

		if(lightningbolt == null)
			return;

		lightningbolt.moveTo(target.position());
		lightningbolt.setCause(user instanceof ServerPlayer ? (ServerPlayer)user : null);
		target.level().addFreshEntity(lightningbolt);

		if(PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, weapon.isDamageableItem()))
		{
			int durabilityConsumed = PropertyModifierComponent.getOrElse(weapon, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 20);
			if(user != null)
				weapon.hurtAndBreak(durabilityConsumed, user, EquipmentSlot.MAINHAND);
			else if(target.level() instanceof ServerLevel serverLevel) weapon.hurtAndBreak(durabilityConsumed, serverLevel, null, (item) -> {});
		}
		else consumeItem(user, weapon, EquipmentSlot.MAINHAND);
	}


//	private final int[] colors = new int[]{0xB5FFFF, 0x1FB2FF};
//	@Override
//	public int getColor(ItemStack stack) {
//		return colors[(int) Math.abs((System.currentTimeMillis() / 10) % colors.length)];
//	}

	@Override
	public int getColor(ItemStack stack) {
		return FastColor.ARGB32.lerp(sparkColor, 0x006EA5, 0xB5FFFF);
	}

	private static final Random random = new Random();
	private static float sparkColor = 0;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	private static void onClientTick(ClientTickEvent.Pre event)
	{
		if(random.nextFloat() < (sparkColor > 0 ? 0.075f : 0.025f))
			sparkColor = 1;
		else sparkColor = Math.max(0, sparkColor- 1/30f);
	}
}
