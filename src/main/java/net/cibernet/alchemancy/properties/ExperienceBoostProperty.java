package net.cibernet.alchemancy.properties;

import net.minecraft.world.entity.Entity;
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
	public void modifyLivingExperienceDrops(Player user, ItemStack weapon, LivingEntity entity, LivingExperienceDropEvent event) {
		event.setDroppedExperience(event.getDroppedExperience() * 2);
	}

	@Override
	public void modifyBlockDrops(Entity breaker, ItemStack tool, List<ItemEntity> drops, BlockDropsEvent event)
	{
		event.setDroppedExperience(event.getDroppedExperience() * 2);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x83FF56;
	}
}
