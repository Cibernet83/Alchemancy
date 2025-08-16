package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.network.S2CEntitySyncTintColorPayload;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyDataAttachments;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@EventBusSubscriber
public class ChromatizeProperty extends Property {

	@Override
	public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
		if(target instanceof LivingEntity living)
			setLivingColor(living, AlchemancyProperties.TINTED.get().getData(stack));
	}

	@Override
	public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {

		if(rayTraceResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living)
			setLivingColor(living, AlchemancyProperties.TINTED.get().getData(stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return AlchemancyProperties.TINTED.get().getColor(stack);
	}

	@SubscribeEvent
	private static void onLogIn(PlayerEvent.PlayerLoggedInEvent event) {

		if(!event.getEntity().level().isClientSide())
			PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new S2CEntitySyncTintColorPayload(event.getEntity()));
	}

	@SubscribeEvent
	private static void onUseFinish(LivingEntityUseItemEvent.Finish event) {

		if(event.getItem().is(Tags.Items.DRINKS_MILK))
			setLivingColor(event.getEntity(), 0xFFFFFFFF);
	}

	@SubscribeEvent
	private static void onStartTracking(PlayerEvent.StartTracking event)
	{
		if(event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity target)
			PacketDistributor.sendToPlayer(player, new S2CEntitySyncTintColorPayload(target));
	}

	public static void setLivingColor(LivingEntity target, Integer... color) {

		target.setData(AlchemancyDataAttachments.ENTITY_TINT.get(), Arrays.stream(color).toList());
		if(!target.level().isClientSide())
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CEntitySyncTintColorPayload(target));
	}
}
