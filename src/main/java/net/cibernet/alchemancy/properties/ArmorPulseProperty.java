package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class ArmorPulseProperty extends Property implements IDataHolder<Boolean>
{
	@Override
	public void modifyDamageReceived(LivingEntity user, ItemStack weapon, EquipmentSlot slot, LivingDamageEvent.Pre event)
	{
		if(!getData(weapon) && (slot.isArmor() || user.getUseItem() == weapon) && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) &&
				event.getSource().getEntity() != null &&
//				event.getSource().getEntity() != user &&
				event.getSource().getEntity().distanceTo(user) <= user.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE))
		{
			setData(weapon, true);
			event.setNewDamage(Math.max(0, event.getNewDamage() - 1));
			InfusedPropertiesHelper.forEachProperty(weapon, propertyHolder -> propertyHolder.value().onActivation(user, user, weapon, event.getSource()));
		}
	}

	@Override
	public void onInventoryTick(Entity user, ItemStack stack, Level level, int inventorySlot, boolean isCurrentItem) {

		if(getData(stack))
			setData(stack, false);
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF7B60;
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("activated");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag(){{putBoolean("activated", data);}};
	}

	@Override
	public Boolean getDefaultData() {
		return false;
	}
}
