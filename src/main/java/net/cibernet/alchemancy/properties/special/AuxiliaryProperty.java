package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.mixin.PlayerMixin;
import net.cibernet.alchemancy.properties.FrostedProperty;
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
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber
public class AuxiliaryProperty extends Property
{
	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem)
	{
		if(user instanceof LivingEntity living)
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onEquippedTick(living, EquipmentSlot.MAINHAND, stack));
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xCAE6E1;
	}

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event)
	{
		if (event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Player user)
			triggerAuxiliaryEffects(user, (holder, stack) -> holder.value().modifyAttackDamage(user, stack, event));

	}

	@SubscribeEvent
	private static void onIncomingDamage(LivingIncomingDamageEvent event)
	{
		if(event.getSource().is(AlchemancyTags.DamageTypes.TRIGGERS_ON_HIT_EFFECTS) && event.getSource().getDirectEntity() instanceof Player user)
			triggerAuxiliaryEffects(user, (propertyHolder, stack) -> propertyHolder.value().onIncomingAttack(user, stack, event.getEntity(), event));
	}

	public static void triggerAuxiliaryEffects(Player user, BiConsumer<Holder<Property>, ItemStack> consumer)
	{
		Inventory inventory = user.getInventory();
		for(int slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			ItemStack stack = inventory.getItem(slot);
			if(InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.AUXILIARY))
				InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> consumer.accept(propertyHolder, stack));
		}
	}
}
