package net.cibernet.alchemancy.properties.soulbind;

import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.properties.special.AuxiliaryProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ClientUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber
public class VengefulProperty extends Property implements IDataHolder<Optional<UUID>>
{
	@Override
	public void modifyAttackDamage(Entity user, ItemStack weapon, LivingDamageEvent.Pre event)
	{
		Optional<UUID> revengeTarget = getData(weapon);

		if(revengeTarget.isPresent() && event.getEntity().getUUID().equals(revengeTarget.get()))
		{
			event.setNewDamage(event.getNewDamage() * 1.85f);
			if(user.level().isClientSide)
				ClientUtil.createTrackedParticles(event.getEntity(), ParticleTypes.ENCHANTED_HIT);
		}
		else event.setNewDamage(event.getNewDamage() * 0.65f);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		Entity source = damageSource.getEntity();
		if(slot == EquipmentSlot.MAINHAND && source != null)
		{
			Optional<UUID> revengeTarget = getData(weapon);
			if(revengeTarget.isEmpty() || !revengeTarget.get().equals(source.getUUID()))
				setData(weapon, Optional.of(source.getUUID()));
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x73171A;
	}

	@Override
	public Optional<UUID> readData(CompoundTag tag) {
		return tag.hasUUID("revenge_target") ? Optional.of(tag.getUUID("revenge_target")) : Optional.empty();
	}

	@Override
	public CompoundTag writeData(Optional<UUID> data) {
		return new CompoundTag(){{
			data.ifPresent(uuid -> putUUID("revenge_target", uuid));
		}};
	}

	@Override
	public Optional<UUID> getDefaultData() {
		return Optional.empty();
	}

	@SubscribeEvent
	public static void livingDamageEvent(LivingDamageEvent.Pre event)
	{
		if(event.getEntity() instanceof Player player)
			AuxiliaryProperty.triggerAuxiliaryEffects(player, ((propertyHolder, stack) ->
			{
				if(propertyHolder.equals(AlchemancyProperties.VENGEFUL))
					propertyHolder.value().modifyDamageReceived(player, stack, EquipmentSlot.MAINHAND, event);
			}));
	}
}
