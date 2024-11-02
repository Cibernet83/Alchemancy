package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PristineProperty extends Property implements IDataHolder<Integer>, ITintModifier
{
	@Override
	public int getColor(ItemStack stack) {
		return ColorUtils.interpolateColorsOverTime(5, 0xff79B7AB, 0xff79B794, 0xff5E85A4, 0xff5E9EA4);
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid)
	{
		if(!getData(stack).equals(getDefaultData()))
		{
			removeData(stack);
			return true;
		}
		return super.onInfusedByDormantProperty(stack, propertySource, grid);
	}

	@Override
	public int modifyDurabilityConsumed(ItemStack stack, LivingEntity user, int originalAmount, int resultingAmount)
	{
		int durability = getData(stack) - 1;

		if(InfusedPropertiesHelper.hasInfusedProperty(stack, asHolder()) && durability <= 0)
			InfusedPropertiesHelper.removeProperty(stack, asHolder());
		setData(stack, durability);

		return 0;
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable("property.detail", super.getName(stack), Component.translatable("property.detail.percentage", (int) (getDurabilityPercentage(stack) * 100))).withColor(getColor(stack));
	}

	private float getDurabilityPercentage(ItemStack stack) {
		return ((float) getData(stack) / getDefaultData());
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

	@Override
	public int getTint(ItemStack stack, int tintIndex, int originalTint, int currentTint) {
		return FastColor.ARGB32.lerp(getDurabilityPercentage(stack) * 0.6f + 0.2f, currentTint, getColor(stack));
	}
}
