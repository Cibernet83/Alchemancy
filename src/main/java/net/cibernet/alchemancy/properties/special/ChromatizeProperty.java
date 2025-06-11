package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.network.EntitySyncTintColorS2CPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@EventBusSubscriber
public class ChromatizeProperty extends Property {

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

		var color = AlchemancyProperties.TINTED.get().getData(weapon);
		target.setData(AlchemancyDataAttachments.ENTITY_TINT.get(), Arrays.stream(color).toList());
		if(!target.level().isClientSide())
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new EntitySyncTintColorS2CPayload(target));
	}

	@Override
	public int getColor(ItemStack stack) {
		return AlchemancyProperties.TINTED.get().getColor(stack);
	}

	@SubscribeEvent
	private static void onLogIn(PlayerEvent.PlayerLoggedInEvent event) {

		if(!event.getEntity().level().isClientSide())
			PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new EntitySyncTintColorS2CPayload(event.getEntity()));
	}
}
