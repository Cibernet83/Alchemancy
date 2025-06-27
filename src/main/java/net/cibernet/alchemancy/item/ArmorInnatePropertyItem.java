package net.cibernet.alchemancy.item;

import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ArmorInnatePropertyItem extends ArmorItem {


	private ResourceLocation armorTexture = null;

	@SafeVarargs
	public ArmorInnatePropertyItem(Holder<ArmorMaterial> material, Type type, Properties properties, Holder<Property>... innateProperties) {
		super(material, type, properties.component(AlchemancyItems.Components.INNATE_PROPERTIES, new InfusedPropertiesComponent(Arrays.asList(innateProperties))));
	}

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		if (armorTexture == null) {
			ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
			armorTexture = ResourceLocation.fromNamespaceAndPath(itemKey.getNamespace(), "textures/models/armor/%s.png".formatted(itemKey.getPath()));
		}
		return armorTexture;
	}


}
