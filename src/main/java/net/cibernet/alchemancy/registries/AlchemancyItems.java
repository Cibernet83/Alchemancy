package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesComponent;
import net.cibernet.alchemancy.item.components.PropertyDataComponent;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.special.ClayMoldProperty;
import net.cibernet.alchemancy.properties.special.WaywardWarpProperty;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.cibernet.alchemancy.Alchemancy.MODID;

public class AlchemancyItems
{
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MODID);

	public static final DeferredItem<BlockItem> BLAZEBLOOM = REGISTRY.registerSimpleBlockItem("blazebloom", AlchemancyBlocks.BLAZEBLOOM);
	public static final DeferredItem<Item> ALCHEMICAL_EXTRACT = REGISTRY.registerSimpleItem("alchemical_extract", new Item.Properties());
	public static final DeferredItem<BlockItem> INFUSION_PEDESTAL = REGISTRY.registerSimpleBlockItem("infusion_pedestal", AlchemancyBlocks.INFUSION_PEDESTAL);
	public static final DeferredItem<BlockItem> ALCHEMANCY_FORGE = REGISTRY.registerSimpleBlockItem("alchemancy_forge", AlchemancyBlocks.ALCHEMANCY_FORGE);
	public static final DeferredItem<BlockItem> ALCHEMANCY_CATALYST = REGISTRY.registerSimpleBlockItem("alchemancy_catalyst", AlchemancyBlocks.ALCHEMANCY_CATALYST);

	public static final DeferredItem<Item> LEAD_INGOT = REGISTRY.registerSimpleItem("lead_ingot");
	public static final DeferredItem<SwordItem> LEAD_SWORD = REGISTRY.register("lead_sword", () -> new SwordItem(Materials.LEAD_TOOLS, new Item.Properties().attributes(SwordItem.createAttributes(Materials.LEAD_TOOLS, 3, -2.4F))));
	public static final DeferredItem<ShovelItem> LEAD_SHOVEL = REGISTRY.register("lead_shovel", () -> new ShovelItem(Materials.LEAD_TOOLS, new Item.Properties().attributes(ShovelItem.createAttributes(Materials.LEAD_TOOLS, 1.5F, -3.0F))));
	public static final DeferredItem<PickaxeItem> LEAD_PICKAXE = REGISTRY.register("lead_pickaxe", () -> new PickaxeItem(Materials.LEAD_TOOLS, new Item.Properties().attributes(PickaxeItem.createAttributes(Materials.LEAD_TOOLS, 1.0F, -2.8F))));
	public static final DeferredItem<AxeItem> LEAD_AXE = REGISTRY.register("lead_axe", () -> new AxeItem(Materials.LEAD_TOOLS, new Item.Properties().attributes(AxeItem.createAttributes(Materials.LEAD_TOOLS, 6.0F, -3.1F))));
	public static final DeferredItem<HoeItem> LEAD_HOE = REGISTRY.register("lead_hoe", () -> new HoeItem(Materials.LEAD_TOOLS, new Item.Properties().attributes(HoeItem.createAttributes(Materials.LEAD_TOOLS, -2.0F, -1.0F))));


	public static final DeferredItem<Item> DREAMSTEEL_INGOT = REGISTRY.registerSimpleItem("dreamsteel_ingot");
	public static final DeferredItem<Item> DREAMSTEEL_NUGGET = REGISTRY.registerSimpleItem("dreamsteel_nugget");
	public static final DeferredItem<SwordItem> DREAMSTEEL_SWORD = REGISTRY.register("dreamsteel_sword", () -> new SwordItem(Materials.DREAMSTEEL_TOOLS, new Item.Properties()
			.component(Components.INFUSION_SLOTS, 6)
			.attributes(PickaxeItem.createAttributes(Materials.DREAMSTEEL_TOOLS, 3, -2.4F))));
	public static final DeferredItem<ShovelItem> DREAMSTEEL_SHOVEL = REGISTRY.register("dreamsteel_shovel", () -> new ShovelItem(Materials.DREAMSTEEL_TOOLS, new Item.Properties()
			.component(Components.INFUSION_SLOTS, 6)
			.attributes(PickaxeItem.createAttributes(Materials.DREAMSTEEL_TOOLS, 1.5F, -3.0F))));
	public static final DeferredItem<PickaxeItem> DREAMSTEEL_PICKAXE = REGISTRY.register("dreamsteel_pickaxe", () -> new PickaxeItem(Materials.DREAMSTEEL_TOOLS, new Item.Properties()
			.component(Components.INFUSION_SLOTS, 6)
			.attributes(PickaxeItem.createAttributes(Materials.DREAMSTEEL_TOOLS, 1.0F, -2.8F))));
	public static final DeferredItem<AxeItem> DREAMSTEEL_AXE = REGISTRY.register("dreamsteel_axe", () -> new AxeItem(Materials.DREAMSTEEL_TOOLS, new Item.Properties()
			.component(Components.INFUSION_SLOTS, 6)
			.attributes(AxeItem.createAttributes(Materials.DREAMSTEEL_TOOLS, 6.0F, -3.1F))));
	public static final DeferredItem<HoeItem> DREAMSTEEL_HOE = REGISTRY.register("dreamsteel_hoe", () -> new HoeItem(Materials.DREAMSTEEL_TOOLS, new Item.Properties()
			.component(Components.INFUSION_SLOTS, 6)
			.attributes(AxeItem.createAttributes(Materials.DREAMSTEEL_TOOLS, -2.0F, -1.0F))));

	public static final DeferredItem<ArmorItem> LEAD_HELMET = REGISTRY.register("lead_helmet", () -> new ArmorItem(Materials.LEAD_ARMOR, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(60))));
	public static final DeferredItem<ArmorItem> LEAD_CHESTPLATE = REGISTRY.register("lead_chestplate", () -> new ArmorItem(Materials.LEAD_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(60))));
	public static final DeferredItem<ArmorItem> LEAD_LEGGINGS = REGISTRY.register("lead_leggings", () -> new ArmorItem(Materials.LEAD_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(60))));
	public static final DeferredItem<ArmorItem> LEAD_BOOTS = REGISTRY.register("lead_boots", () -> new ArmorItem(Materials.LEAD_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(60))));

	public static final DeferredItem<Item> BLANK_PEARL = REGISTRY.registerSimpleItem("blank_pearl");
	public static final DeferredItem<Item> REVEALING_PEARL = REGISTRY.registerSimpleItem("revealing_pearl");
	public static final DeferredItem<Item> PARADOX_PEARL = REGISTRY.registerSimpleItem("paradox_pearl");
	public static final DeferredItem<Item> GLOWING_ORB = REGISTRY.register("glowing_orb", () -> new BlockItem(AlchemancyBlocks.GLOWING_ORB.get(), new Item.Properties()));

	public static final DeferredItem<Item> MICROSCOPIC_LENS = REGISTRY.registerSimpleItem("microscopic_lens");
	public static final DeferredItem<Item> MACROSCOPIC_LENS = REGISTRY.registerSimpleItem("macroscopic_lens");

	public static final DeferredItem<InnatePropertyItem> IRON_RING = REGISTRY.register("pearlescent_ring", () -> new InnatePropertyItem.Builder().auxiliary(false).stacksTo(1).infusionSlots(1).build());
	public static final DeferredItem<InnatePropertyItem> ETERNAL_GLOW_RING = REGISTRY.register("eternal_glow_ring", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.ETERNAL_GLOW).auxiliary(true).toggleable(true).stacksTo(1).build());
	public static final DeferredItem<InnatePropertyItem> PHASING_RING = REGISTRY.register("phasing_ring", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.PHASE_STEP).auxiliary(true).toggleable(true).stacksTo(1).build());
	public static final DeferredItem<InnatePropertyItem> UNDYING_RING = REGISTRY.register("undying_ring", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.DEATH_WARD).auxiliary(true).stacksTo(1).build());
	public static final DeferredItem<InnatePropertyItem> FRIENDSHIP_RING = REGISTRY.register("friendship_ring", () -> new InnatePropertyItem.Builder().withProperties(AlchemancyProperties.FRIENDLY).auxiliary(true).toggleable(true).stacksTo(1).build());

	public static final DeferredItem<InnatePropertyItem> PROPERTY_VISOR = REGISTRY.register("property_visor", () -> new InnatePropertyItem.Builder()
			.withProperties(AlchemancyProperties.HEADWEAR, AlchemancyProperties.REVEALING)
			.addModifier(AlchemancyProperties.HEADWEAR, AlchemancyProperties.Modifiers.ON_RIGHT_CLICK, true)
			.stacksTo(1)
			.build());
	public static final DeferredItem<InnatePropertyItem> WAYWARD_MEDALLION = REGISTRY.register("wayward_medallion", () -> new InnatePropertyItem.Builder()
			.withProperties(AlchemancyProperties.WAYWARD_WARP)
			.durability(160, Ingredient.of(Items.ENDER_PEARL))
			.tooltip(WaywardWarpProperty.MEDALLION_TOOLTIP)
			.stacksTo(1)
			.build());
	public static final DeferredItem<InnatePropertyItem> ROCKET_POWERED_HAMMER = REGISTRY.register("rocket_powered_hammer", () -> new InnatePropertyItem.Builder()
			.withProperties(AlchemancyProperties.ROCKET_POWERED, AlchemancyProperties.DENSE)
			.durability(600)
			.stacksTo(1)
			.build(new Item.Properties()
					.rarity(Rarity.EPIC)
					.attributes(MaceItem.createAttributes())
					.component(DataComponents.TOOL, MaceItem.createToolProperties())));
	public static final DeferredItem<InnatePropertyItem> BARRELS_WARHAMMER = REGISTRY.register("barrels_warhammer", () -> new InnatePropertyItem.Builder()
			.withProperties(AlchemancyProperties.ROCKET_POWERED, AlchemancyProperties.DENSE)
			.durability(600)
			.stacksTo(1)
			.build(new Item.Properties()
					.rarity(Rarity.EPIC)
					.attributes(MaceItem.createAttributes())
					.component(DataComponents.TOOL, MaceItem.createToolProperties())));
	public static final DeferredItem<InnatePropertyItem> UNSHAPED_CLAY = REGISTRY.register("unshaped_clay", () -> new InnatePropertyItem.Builder()
			.withProperties(AlchemancyProperties.CLAY_MOLD)
			.tooltip(ClayMoldProperty.ITEM_TOOLTIP)
			.build());

	public static final DeferredItem<Item> PROPERTY_CAPSULE = REGISTRY.registerItem("property_capsule", properties -> new Item(properties.rarity(Rarity.EPIC)));

	public static class Materials
	{
		public static final Tier LEAD_TOOLS = new SimpleTier(AlchemancyTags.Blocks.INCORRECT_FOR_LEAD_TOOL, 1000, 6, 1.5f, 10, () -> Ingredient.of(AlchemancyTags.Items.REPAIRS_LEAD));
		public static final Tier DREAMSTEEL_TOOLS = new SimpleTier(AlchemancyTags.Blocks.INCORRECT_FOR_DREAMSTEEL_TOOL, 1561, 9.0F, 4.0F, 15, () -> Ingredient.of(AlchemancyTags.Items.REPAIRS_DREAMSTEEL));


		public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL_REGISTRY = DeferredRegister.create(Registries.ARMOR_MATERIAL, MODID);

		public static final Holder<ArmorMaterial> LEAD_ARMOR = registerArmor("lead", 10, 0, 0.1f,
				() -> Ingredient.of(LEAD_INGOT),
			Util.make(new EnumMap<>(ArmorItem.Type.class), p_323378_ -> {
			p_323378_.put(ArmorItem.Type.BOOTS, 2);
			p_323378_.put(ArmorItem.Type.LEGGINGS, 5);
			p_323378_.put(ArmorItem.Type.CHESTPLATE, 6);
			p_323378_.put(ArmorItem.Type.HELMET, 2);
			p_323378_.put(ArmorItem.Type.BODY, 5);
		}));

		private static Holder<ArmorMaterial> registerArmor(String key, int enchantmentValue, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial, Map<ArmorItem.Type, Integer> defenseMap)
		{
			return ARMOR_MATERIAL_REGISTRY.register(key, () -> new ArmorMaterial(defenseMap, enchantmentValue, SoundEvents.ARMOR_EQUIP_IRON, repairMaterial, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, key))),
					toughness, knockbackResistance));
		}
	}

	public static class Components
	{
		public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INFUSION_SLOTS = REGISTRY.register("infusion_slots", () ->
				new DataComponentType.Builder<Integer>()
				.persistent(ExtraCodecs.NON_NEGATIVE_INT)
				.networkSynchronized(ByteBufCodecs.VAR_INT).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> INFUSED_PROPERTIES = REGISTRY.register("infused_properties", () ->
				new DataComponentType.Builder<InfusedPropertiesComponent>()
				.persistent(InfusedPropertiesComponent.CODEC)
				.networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> INNATE_PROPERTIES = REGISTRY.register("innate_properties", () ->
				new DataComponentType.Builder<InfusedPropertiesComponent>()
				.persistent(InfusedPropertiesComponent.CODEC)
				.networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusedPropertiesComponent>> STORED_PROPERTIES = REGISTRY.register("stored_properties", () ->
				new DataComponentType.Builder<InfusedPropertiesComponent>()
				.persistent(InfusedPropertiesComponent.CODEC)
				.networkSynchronized(InfusedPropertiesComponent.STREAM_CODEC).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<PropertyDataComponent>> PROPERTY_DATA = REGISTRY.register("property_data", () ->
				new DataComponentType.Builder<PropertyDataComponent>()
				.persistent(PropertyDataComponent.CODEC)
				.networkSynchronized(PropertyDataComponent.STREAM_CODEC).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<PropertyModifierComponent>> PROPERTY_MODIFIERS = REGISTRY.register("property_modifiers", () ->
				new DataComponentType.Builder<PropertyModifierComponent>()
				.persistent(PropertyModifierComponent.CODEC)
				.networkSynchronized(PropertyModifierComponent.STREAM_CODEC).build());

		public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> INGREDIENT_DISPLAY = REGISTRY.register("ingredient_display", () ->
				new DataComponentType.Builder<Unit>()
				.persistent(Unit.CODEC)
				.networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build());


		/*  TODO

		 *  Dreamsteel Armor: Obtained by infusing the appropriate Properties/Items + Lustrous onto Dreamsteel. +2 Property Slots. Same tint dealeo as Dreamsteel
		 * Pearlescent Block, Rod, and Crystal: Interacts with properties to create new items
		 */


	}
}
