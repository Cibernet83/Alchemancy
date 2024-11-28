package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class HeadearProperty extends Property
{
	@Override
	public EquipmentSlot modifyWearableSlot(ItemStack stack, @Nullable EquipmentSlot originalSlot, @Nullable EquipmentSlot slot) {
		return EquipmentSlot.HEAD;
	}

	@Override
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
	{
		if(PropertyModifierComponent.get(event.getItemStack(), asHolder(), AlchemancyProperties.Modifiers.ON_RIGHT_CLICK))
		{
			Player player = event.getEntity();
			InteractionResultHolder<ItemStack> resultHolder = swapWithEquipmentSlot(event.getItemStack().getItem(), event.getLevel(), player, event.getHand());
			if(resultHolder.getResult().consumesAction())
				player.setItemInHand(event.getHand(), resultHolder.getObject());
			event.setCancellationResult(resultHolder.getResult());
			
			if(!(event.getItemStack().getItem() instanceof Equipable))
			{
				player.level()
						.playSeededSound(
								null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, player.getSoundSource(), 1.0F, 1.0F, player.getRandom().nextLong()
						);
			}
		}
	}


	public static InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item item, Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		EquipmentSlot equipmentslot = player.getEquipmentSlotForItem(itemstack);
		if (!player.canUseSlot(equipmentslot)) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
			if ((!EnchantmentHelper.has(itemstack1, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || player.isCreative())
					&& !ItemStack.matches(itemstack, itemstack1)) {
				if (!level.isClientSide()) {
					player.awardStat(Stats.ITEM_USED.get(item));
				}

				ItemStack itemstack2 = itemstack1.isEmpty() ? itemstack : itemstack1.copyAndClear();
				ItemStack itemstack3 = player.isCreative() ? itemstack.copy() : itemstack.copyAndClear();
				player.setItemSlot(equipmentslot, itemstack3);
				return InteractionResultHolder.sidedSuccess(itemstack2, level.isClientSide());
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xE24F74;
	}
}
