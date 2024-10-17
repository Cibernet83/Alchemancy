package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResizedProperty extends Property implements IDataHolder<Float>
{
	private static final ResourceLocation MODIFIER_KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "resized_modifier");

	public static final float MIN = 0.5f;
	public static final float MAX = 2f;

	@Override
	public void applyAttributes(ItemAttributeModifierEvent event)
	{
		float size = getData(event.getItemStack());
		event.addModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier(MODIFIER_KEY, size - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND);
		event.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(MODIFIER_KEY, 1/size - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND);
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid)
	{
		//boolean hasMatching = super.onInfusedByDormantProperty(stack, propertySource, grid);
		float currentSize = getData(stack);

		if(propertySource.is(AlchemancyTags.Items.INCREASES_RESIZED) && currentSize < MAX) {
			setData(stack, Math.min(MAX, currentSize + 0.1f));
		}
		else if(propertySource.is(AlchemancyTags.Items.DECREASES_RESIZED) && currentSize > MIN) {
			setData(stack, Math.max(MIN, currentSize - 0.1f));
		} else if (!getData(propertySource).equals(getDefaultData()))
			setData(stack, getData(propertySource));
		else return false;
		return true;
	}

	@Override
	public Float readData(CompoundTag tag) {
		return tag.getFloat("size");
	}

	@Override
	public CompoundTag writeData(Float data)
	{
		return new CompoundTag(){{putFloat("size", data);}};
	}

	@Override
	public Float getDefaultData() {
		return 1f;
	}

	@Override
	public int getColor(ItemStack stack) {
		return 0xDE0AFF;
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable("property.detail", super.getName(stack), Component.translatable("property.detail.percentage", (int)(getData(stack) * 100))).withColor(getColor(stack));
	}

	@Override
	public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder)
	{
		ArrayList<ItemStack> result = new ArrayList<>();

		for(float size = 0.5f; size <= 2 ; size += 1.5f)
		{
			ItemStack stack = capsuleItem.toStack();
			stack.set(AlchemancyItems.Components.STORED_PROPERTIES, new InfusedPropertiesComponent(List.of(holder)));
			setData(stack, size);
			result.add(stack);
		}

		return result;
	}
}
