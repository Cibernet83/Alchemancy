package net.cibernet.alchemancy.properties;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Optional;

public class ShieldingProperty extends Property
{
	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		event.getEntity().startUsingItem(event.getHand());
		event.setCancellationResult(InteractionResult.CONSUME);
		event.setCanceled(true);
	}

	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if(user.getUseItem() == weapon && event.getNewDamage() > 0 && user.isDamageSourceBlocked(event.getSource()))
		{

			event.setNewDamage(event.getNewDamage() * 0.5f);

			if(weapon.isDamageableItem())
				weapon.hurtAndBreak(1, user, user.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
			else
			{
				user.onEquippedItemBroken(weapon.getItem(), user.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
				weapon.consume(1, user);
			}
		}
	}

	@Override
	public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
		return current.isEmpty() ? Optional.of(UseAnim.BLOCK) : Optional.empty();
	}

	@Override
	public int modifyUseDuration(ItemStack stack, int original, int result) {
		return 72000;
	}

	@Override
	public int getPriority() {
		return Priority.HIGHER;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x91939B;
	}
}
