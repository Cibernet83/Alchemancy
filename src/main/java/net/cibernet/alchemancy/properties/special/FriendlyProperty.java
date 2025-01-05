package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

@EventBusSubscriber
public class FriendlyProperty extends Property
{
	@Override
	public void onIncomingAttack(Entity user, ItemStack weapon, LivingEntity target, LivingIncomingDamageEvent event)
	{
		if(target.getType().is(AlchemancyTags.EntityTypes.AFFECTED_BY_FRIENDLY))
			event.setCanceled(true);
	}

	@Override
	public int getPriority() {
		return Priority.LOWEST;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDC989E;
	}

	@Override
	public boolean hasJournalEntry() {
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onIncomingDamage(LivingIncomingDamageEvent event)
	{
		if(!(event.isCanceled() || event.getEntity().equals(event.getSource().getEntity()) || !event.getEntity().getType().is(AlchemancyTags.EntityTypes.AFFECTED_BY_FRIENDLY)) &&
				event.getSource().getEntity() instanceof LivingEntity user &&
				InfusedPropertiesHelper.hasItemWithProperty(user, AlchemancyProperties.FRIENDLY, true))
			event.setCanceled(true);
	}
}
