package net.cibernet.alchemancy.registries;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;

public class AlchemancyTags
{
	public static class Items
	{
        public static final TagKey<Item> INGOTS_LEAD = registerCommonTag(Registries.ITEM, "ingots/lead");
        public static final TagKey<Item> INGOTS_DREAMSTEEL = registerTag(Registries.ITEM, "ingots/dreamsteel");

        public static final TagKey<Item> IMMUNE_TO_INFUSIONS = registerTag(Registries.ITEM, "immune_to_infusions");
        public static final TagKey<Item> REMOVES_INFUSIONS = registerTag(Registries.ITEM, "removes_infusions");

        public static final TagKey<Item> INCREASES_SHOCK_DAMAGE_RECEIVED = registerTag(Registries.ITEM, "doubles_shock_damage_received");

        public static final TagKey<Item> TINT_BASE_LAYER = registerTag(Registries.ITEM, "tint_base_layer");
        public static final TagKey<Item> DONT_TINT_BASE_LAYER = registerTag(Registries.ITEM, "dont_tint_base_layer");
        public static final TagKey<Item> INCREASES_RESIZED = registerTag(Registries.ITEM, "increases_resized");
        public static final TagKey<Item> DECREASES_RESIZED = registerTag(Registries.ITEM, "decreases_resized");
		public static final TagKey<Item> INFUSION_REMOVES_GLINT = registerTag(Registries.ITEM, "infusion_removes_glint");

		public static final TagKey<Item> DISABLES_COMPACT = registerTag(Registries.ITEM, "disables_compact");
	}

	public static class Blocks
	{
		public static final TagKey<Block> ALCHEMANCY_CRYSTAL_CATALYSTS = registerTag(Registries.BLOCK, "alchemancy_crystal_catalysts");
		public static final TagKey<Block> INCORRECT_FOR_LEAD_TOOL = registerTag(Registries.BLOCK, "incorrect_for_lead_tool");
		public static final TagKey<Block> INCORRECT_FOR_DREAMSTEEL_TOOL = registerTag(Registries.BLOCK, "incorrect_for_dreamsteel_tool");
		public static final TagKey<Block> SUPPORTS_BLAZEBLOOM = registerTag(Registries.BLOCK, "supports_blazebloom");
		public static final TagKey<Block> REQUIRED_FOR_BLAZEBLOOM_GENERATION = registerTag(Registries.BLOCK, "required_for_blazebloom_generation");
		public static final TagKey<Block> WAYFINDING_TARGETABLE = registerTag(Registries.BLOCK, "wayfinding_targetable");
		public static final TagKey<Block> BROKEN_BY_HARDENED = registerTag(Registries.BLOCK, "broken_by_hardened");
	}

	public static class DamageTypes
	{
        public static final TagKey<DamageType> TRIGGERS_ON_HIT_EFFECTS = registerTag(Registries.DAMAGE_TYPE, "triggers_on_hit_effects");
        public static final TagKey<DamageType> TRIGGERS_ON_PROJECTILE_HIT_EFFECTS = registerTag(Registries.DAMAGE_TYPE, "triggers_on_projectile_hit_effects");
        public static final TagKey<DamageType> SHOCK_DAMAGE = registerTag(Registries.DAMAGE_TYPE, "shock_damage");
        public static final TagKey<DamageType> ARCANE_DAMAGE = registerTag(Registries.DAMAGE_TYPE, "arcane_damage");
	}

	public static class EntityTypes
	{
        public static final TagKey<EntityType<?>> TEMPTED_BY_SWEET = registerTag(Registries.ENTITY_TYPE, "tempted_by_sweet_property");
        public static final TagKey<EntityType<?>> TEMPTED_BY_WEALTHY = registerTag(Registries.ENTITY_TYPE, "tempted_by_wealthy_property");
        public static final TagKey<EntityType<?>> SCARED_BY_SCARY = registerTag(Registries.ENTITY_TYPE, "scared_by_scary_property");
        public static final TagKey<EntityType<?>> AGGROED_BY_SEEDED = registerTag(Registries.ENTITY_TYPE, "aggroed_by_seeded_property");
        public static final TagKey<EntityType<?>> CANNOT_CAPTURE = registerTag(Registries.ENTITY_TYPE, "cannot_capture");
		public static final TagKey<EntityType<?>> AFFECTED_BY_FRIENDLY = registerTag(Registries.ENTITY_TYPE, "affected_by_friendly");
	}

	public static class Dimensions
	{
		public static final TagKey<DimensionType> DEPTH_DWELLER_EFFECTIVE = registerTag(Registries.DIMENSION_TYPE, "depth_dweller_effective");
		public static final TagKey<DimensionType> WAYFINDING_POINTS_TO_ORIGIN = registerTag(Registries.DIMENSION_TYPE, "wayfinding_points_to_origin");
	}

	public static class Properties
	{
		public static final TagKey<Property> DISABLES_BLOCK_ATTACK_IN_CREATIVE = registerTag(AlchemancyProperties.REGISTRY_KEY, "disables_block_attack_in_creative");
	}

	public static class DataComponents
	{
		public static final TagKey<DataComponentType<?>> DISABLES_COMPACT = registerTag(Registries.DATA_COMPONENT_TYPE, "disables_compact");
	}

	public static final class Enchantments
	{
		public static final TagKey<Enchantment> BUFFS_BURNING = registerTag(Registries.ENCHANTMENT, "buffs_burning");
	}

	private static <T> TagKey<T> registerCommonTag(ResourceKey<Registry<T>> registry, String key)
	{
		return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath("c", key));
	}

	private static <T> TagKey<T> registerTag(ResourceKey<Registry<T>> registry, String key)
	{
		return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, key));
	}
}
