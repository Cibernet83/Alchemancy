package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class WaxedProperty extends Property implements IDataHolder<Integer>
{
	@Override
	public int getColor(ItemStack stack)
	{
		return 0xFABF29;
	}


	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid) {
		if (!getData(stack).equals(getDefaultData())) {
			removeData(stack);
			return true;
		}
		return super.onInfusedByDormantProperty(stack, propertySource, grid);
	}

	@Override
	public void onIncomingDamageReceived(Entity user, ItemStack stack, EquipmentSlot slot, DamageSource source, LivingIncomingDamageEvent event)
	{
		if(event.getSource().is(DamageTypeTags.IS_FIRE) && !event.isCanceled() && slot.isArmor())
		{
			if(user.invulnerableTime <= 0)
			{
				if (InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder())) {
					int durability = getData(stack) - 1;
					if (durability <= 0)
						InfusedPropertiesHelper.removeProperty(stack, asHolder());
					else setData(stack, durability);
				}

				user.invulnerableTime = 10;
			}
			event.setCanceled(true);
		}
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable("property.detail", super.getName(stack), Component.translatable("property.detail.percentage", (int)(getData(stack) * 100 / getDefaultData()))).withColor(getColor(stack));
	}

	@Override
	public Integer readData(CompoundTag tag) {
		return tag.contains("durability", Tag.TAG_INT) ? tag.getInt("durability") : getDefaultData();
	}

	@Override
	public CompoundTag writeData(Integer data) {
		return new CompoundTag(){{putInt("durability", data);}};
	}

	@Override
	public Integer getDefaultData() {
		return 100;
	}
}
