package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.List;

public class ExperienceBoostProperty extends Property
{
	@Override
	public void modifyLivingExperienceDrops(Player user, ItemStack weapon, EquipmentSlot slot, LivingEntity entity, LivingExperienceDropEvent event)
	{
		if(slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.BODY)
			event.setDroppedExperience(event.getDroppedExperience() * 2);
		else event.setDroppedExperience((int) Math.floor(event.getDroppedExperience() * 1.1f));
	}

	@Override
	public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event)
	{
		if(slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.BODY)
			event.setDroppedExperience(event.getDroppedExperience() * 2);
		else event.setDroppedExperience((int) Math.floor(event.getDroppedExperience() * 1.1f));
	}

	@Override
	public int modifyEnchantmentValue(int originalValue, int result) {
		return result + 5;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x83FF56;
	}
}
