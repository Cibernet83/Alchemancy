package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class VoidtouchProperty extends Property
{
	@Override
	public void onRightClickEntity(PlayerInteractEvent.EntityInteract event)
	{
		destroyEntity(event.getTarget(), event.getItemStack(), event.getEntity(), event.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
	}

	@Override
	public void onAttack(@Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target) {

		destroyEntity(target, weapon, user, EquipmentSlot.MAINHAND);
	}

	private void destroyEntity(Entity target, ItemStack stack, @Nullable Entity user, EquipmentSlot breakSlot)
	{
		if(target instanceof Player player)
		{
			player.kill();
			if(player.isDeadOrDying())
				player.discard();
		}
		else target.discard();

		if(!PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.PREVENT_CONSUMPTION, false))
			consumeItem(user, stack, breakSlot);
		else if(stack.isDamageableItem() && user instanceof LivingEntity living)
			stack.hurtAndBreak(PropertyModifierComponent.getOrElse(stack, asHolder(), AlchemancyProperties.Modifiers.DURABILITY_CONSUMPTION, 10), living,
					breakSlot);
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
		return List.of();
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0;
	}
}
