package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class DrippingProperty extends Property
{
	@Override
	public int getColor(ItemStack stack) {
		return 0xA08D71;
	}

	@Override
	public void onAttack(@org.jetbrains.annotations.Nullable Entity user, ItemStack weapon, DamageSource damageSource, LivingEntity target)
	{
		drip(target.level(), user, target, weapon);
	}

	@Override
	public void onDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, DamageSource damageSource)
	{
		drip(user.level(), user, user, weapon);
	}

	public boolean drip(@Nullable Level level, Entity user, @Nullable Entity target, ItemStack stack)
	{
		if(target == null && user == null)
			return false;

		if(target == null)
			target = user;

		return AlchemancyProperties.BUCKETING.get().placeLiquid(level, target.blockPosition(), stack, target instanceof Player player ? player : null, null) ||
				(user != null && AlchemancyProperties.HOLLOW.get().dropItems(stack, user)) ||
				AlchemancyProperties.CAPTURING.get().releaseMob(level, stack, target.position(), null);
	}

	@Override
	public @org.jetbrains.annotations.Nullable ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult)
	{
		return canMilkItem(root.getItem()) && attemptMilk(user, hand) ? ItemInteractionResult.sidedSuccess(user.level().isClientSide) :
				super.onRootedRightClick(root, user, hand, hitResult);
	}

	@SubscribeEvent
	private static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
	{
		if(event.getTarget() instanceof LivingEntity target)
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if(slot.isArmor())
				{
					ItemStack stack = target.getItemBySlot(slot);
					if(canMilkItem(stack) && attemptMilk(event.getEntity(), event.getHand()))
					{
						event.setCancellationResult(InteractionResult.sidedSuccess(event.getEntity().level().isClientSide));
						event.setCanceled(true);
						return;
					}
				}
			}
	}

	private static boolean canMilkItem(ItemStack stack) {
		return InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.DRIPPING) &&
				InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.CALCAREOUS);
	}

	private static boolean attemptMilk(Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(Items.BUCKET)) {
			player.playSound(AlchemancySoundEvents.CALCAREOUS_MILK.value(), 1.0F, 1.0F);
			ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
			player.setItemInHand(hand, itemstack1);
			return true;
		}
		return false;
	}
}
