package net.cibernet.alchemancy.properties.special;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.InteractableProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RunningStartProperty extends Property implements IDataHolder<Boolean> {

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
		super.onEquippedTick(user, slot, stack);

		if (!user.isSprinting()) return;

		if (user.isSprinting() && !getData(stack) && (!(user instanceof Player player) || !player.getCooldowns().isOnCooldown(stack.getItem()))) {
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(user, user, stack));

			if (user instanceof Player player)
				InteractableProperty.applyCooldown(player, stack, 80);
		}
	}


	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {
		if (user.isSprinting()) setData(stack, true);
		else removeData(stack);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x5BFFC3;
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("sprinting");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag() {{
			putBoolean("sprinting", data);
		}};
	}

	@Override
	public Boolean getDefaultData() {
		return false;
	}
}
