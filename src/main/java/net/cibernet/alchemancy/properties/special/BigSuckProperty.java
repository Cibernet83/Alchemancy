package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

public class BigSuckProperty extends Property
{
	@Override
	public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {

		if(level instanceof ServerLevel serverLevel && user instanceof Player player)
			for ( ItemEntity item : serverLevel.getEntities(EntityTypeTest.forClass(ItemEntity.class), item -> true)) {
				item.playerTouch(player);
			}
		return false;
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
