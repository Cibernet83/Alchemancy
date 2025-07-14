package net.cibernet.alchemancy.properties;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class AssimilatingProperty extends Property
{
	@Override
	public void onStackedOverMe(ItemStack otherStack, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled)
	{
		if(stack != otherStack && ItemStack.isSameItem(stack, otherStack) && (stack.isDamaged() ||
				(!InfusedPropertiesHelper.getInfusedProperties(otherStack).isEmpty() && InfusedPropertiesHelper.getRemainingInfusionSlots(stack) > 0 ) ||
				EnchantmentHelper.hasAnyEnchantments(otherStack)))
		{
			repairItem(stack, (stack.getMaxDamage() - otherStack.getDamageValue()));

			combineEnchantments(stack, otherStack);
			stackedOnSlot.set(combineInfusions(player.level(), stack, otherStack));

			otherStack.shrink(1);
			isCancelled.set(true);
		}
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if(stack.isDamageableItem() && user instanceof Player player)
		{
			for (int i = 0; i < player.getInventory().getContainerSize(); i++)
			{
				ItemStack otherStack = player.getInventory().getItem(i);
				if(stack != otherStack && ItemStack.isSameItem(stack, otherStack) && (shouldRepair(stack) || stack.getDamageValue() >= stack.getMaxDamage() - otherStack.getDamageValue()))
				{
					repairItem(stack, (stack.getMaxDamage() - otherStack.getDamageValue()));

					if(stack == user.getItemBySlot(slot))
					{
						combineEnchantments(stack, otherStack);
						user.setItemSlot(slot, combineInfusions(user.level(), stack, otherStack));
					}

					player.getInventory().removeItem(i, 1);
					return;
				}
			}
		}
	}

	public static boolean shouldRepair(ItemStack stack)
	{
		return canRepair(stack, 5);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x62F767;
	}

	private ItemStack combineInfusions(Level level, ItemStack to, ItemStack from) {

		var toCombine = InfusedPropertiesHelper.getInfusedProperties(from);
		InfusedPropertiesHelper.addProperties(to, toCombine);

		for (Holder<Property> property : toCombine) {
			if(property.value() instanceof IDataHolder<?> dataHolder)
				dataHolder.combineDataAndSet(to, from);
		}

		to = ForgeRecipeGrid.resolveInteractions(to, level);
		return to;
	}

	private void combineEnchantments(ItemStack to, ItemStack from) {

		ItemEnchantments.Mutable itemenchantments$mutable = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(to));

		ItemEnchantments itemenchantments = EnchantmentHelper.getEnchantmentsForCrafting(from);
		boolean flag2 = false;
		boolean flag3 = false;

		for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemenchantments.entrySet()) {
			Holder<Enchantment> holder = entry.getKey();
			int i2 = itemenchantments$mutable.getLevel(holder);
			int j2 = entry.getIntValue();
			j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
			Enchantment enchantment = holder.value();

			boolean supportsEnchantment = to.supportsEnchantment(holder);
			for (Holder<Enchantment> holder1 : itemenchantments$mutable.keySet()) {
				if (!holder1.equals(holder) && !Enchantment.areCompatible(holder, holder1)) {
					supportsEnchantment = false;
				}
			}

			if (supportsEnchantment) {
				if (j2 > enchantment.getMaxLevel()) {
					j2 = enchantment.getMaxLevel();
				}

				itemenchantments$mutable.set(holder, j2);
			}
		}


		EnchantmentHelper.setEnchantments(to, itemenchantments$mutable.toImmutable());
	}
}
