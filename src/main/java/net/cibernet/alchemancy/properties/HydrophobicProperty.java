package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class HydrophobicProperty extends Property implements IDataHolder<Boolean>
{
	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack)
	{
		if(user.isInWater() && !getData(stack))
			InfusedPropertiesHelper.forEachProperty(stack, propertyHolder -> propertyHolder.value().onActivation(user, user, stack));

		setData(stack, user.isInWater());
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0x329BD3;
	}

	@Override
	public Boolean readData(CompoundTag tag) {
		return tag.getBoolean("in_water");
	}

	@Override
	public CompoundTag writeData(Boolean data) {
		return new CompoundTag(){{putBoolean("in_water", data);}};
	}

	@Override
	public Boolean getDefaultData() {
		return false;
	}
}
