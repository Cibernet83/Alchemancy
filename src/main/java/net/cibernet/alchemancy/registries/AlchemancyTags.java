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
		public static final TagKey<Item> REPAIRS_LEAD = registerTag(Registries.ITEM, "repairs_lead");
		public static final TagKey<Item> REPAIRS_DREAMSTEEL = registerTag(Registries.ITEM, "repairs_dreamsteel");
		public static final TagKey<Item> REPAIRS_FLAME_EMPEROR_TOOL = registerTag(Registries.ITEM, "repairs_flame_emperor_tool");
		public static final TagKey<Item> REPAIRS_UNSHAPED_CLAY = registerTag(Registries.ITEM, "repairs_unshaped_clay");

        public static final TagKey<Item> IMMUNE_TO_INFUSIONS = registerTag(Registries.ITEM, "immune_to_infusions");
        public static final TagKey<Item> REMOVES_INFUSIONS = registerTag(Registries.ITEM, "removes_infusions");
        public static final TagKey<Item> DISABLES_INFUSION_ABILITIES = registerTag(Registries.ITEM, "disables_infusion_abilities");

        public static final TagKey<Item> INCREASES_SHOCK_DAMAGE_RECEIVED = registerTag(Registries.ITEM, "doubles_shock_damage_received");

        public static final TagKey<Item> TINT_BASE_LAYER = registerTag(Registries.ITEM, "tint_base_layer");
        public static final TagKey<Item> DONT_TINT_BASE_LAYER = registerTag(Registries.ITEM, "dont_tint_base_layer");
        public static final TagKey<Item> INCREASES_RESIZED = registerTag(Registries.ITEM, "increases_resized");
        public static final TagKey<Item> DECREASES_RESIZED = registerTag(Registries.ITEM, "decreases_resized");
		public static final TagKey<Item> INFUSION_REMOVES_GLINT = registerTag(Registries.ITEM, "infusion_removes_glint");

		public static final TagKey<Item> DISABLES_COMPACT = registerTag(Registries.ITEM, "disables_compact");
		public static final TagKey<Item> TRIGGERS_HEARTY = registerTag(Registries.ITEM, "triggers_hearty_on_use");

		public static final TagKey<Item> CODEX_DISCOVERY_ON_PICKUP = registerTag(Registries.ITEM, "codex_discovery_on_pickup");
		public static final TagKey<Item> IS_INFUSED = registerTag(Registries.ITEM, "is_infused");
	}

	public static class Blocks
	{
		public static final TagKey<Block> ALCHEMANCY_CRYSTAL_CATALYSTS = registerTag(Registries.BLOCK, "alchemancy_crystal_catalysts");
		public static final TagKey<Block> INCORRECT_FOR_LEAD_TOOL = registerTag(Registries.BLOCK, "incorrect_for_lead_tool");
		public static final TagKey<Block> INCORRECT_FOR_DREAMSTEEL_TOOL = registerTag(Registries.BLOCK, "incorrect_for_dreamsteel_tool");
		public static final TagKey<Block> INCORRECT_FOR_FLAME_EMPEROR_TOOL = registerTag(Registries.BLOCK, "incorrect_for_flame_emperor_tool");
		public static final TagKey<Block> SUPPORTS_BLAZEBLOOM = registerTag(Registries.BLOCK, "supports_blazebloom");
		public static final TagKey<Block> REQUIRED_FOR_BLAZEBLOOM_GENERATION = registerTag(Registries.BLOCK, "required_for_blazebloom_generation");
		public static final TagKey<Block> WAYFINDING_TARGETABLE = registerTag(Registries.BLOCK, "wayfinding_targetable");
		public static final TagKey<Block> BROKEN_BY_HARDENED = registerTag(Registries.BLOCK, "broken_by_hardened");
		public static final TagKey<Block> MAGNETIC_STICKS_TO = registerTag(Registries.BLOCK, "magnetic_sticks_to");
		public static final TagKey<Block> CANNOT_ENCAPSULATE = registerTag(Registries.BLOCK, "cannot_encapsulate");
		public static final TagKey<Block> ENCAPSULATING_ALWAYS_PLACES = registerTag(Registries.BLOCK, "encapsulating_always_places");
	}

	public static class DamageTypes
	{
        public static final TagKey<DamageType> TRIGGERS_ON_HIT_EFFECTS = registerTag(Registries.DAMAGE_TYPE, "triggers_on_hit_effects");
        public static final TagKey<DamageType> TRIGGERS_ON_PROJECTILE_HIT_EFFECTS = registerTag(Registries.DAMAGE_TYPE, "triggers_on_projectile_hit_effects");
        public static final TagKey<DamageType> SHOCK_DAMAGE = registerTag(Registries.DAMAGE_TYPE, "shock_damage");
        public static final TagKey<DamageType> ARCANE_DAMAGE = registerTag(Registries.DAMAGE_TYPE, "arcane_damage");
        public static final TagKey<DamageType> AFFECTED_BY_MAGIC_RESISTANT = registerTag(Registries.DAMAGE_TYPE, "affected_by_magic_resistant");
	}

	public static class EntityTypes
	{
        public static final TagKey<EntityType<?>> TEMPTED_BY_SWEET = registerTag(Registries.ENTITY_TYPE, "tempted_by_sweet_property");
        public static final TagKey<EntityType<?>> TEMPTED_BY_WEALTHY = registerTag(Registries.ENTITY_TYPE, "tempted_by_wealthy_property");
        public static final TagKey<EntityType<?>> SCARED_BY_SCARY = registerTag(Registries.ENTITY_TYPE, "scared_by_scary_property");
        public static final TagKey<EntityType<?>> AGGROED_BY_SEEDED = registerTag(Registries.ENTITY_TYPE, "aggroed_by_seeded_property");
        public static final TagKey<EntityType<?>> CANNOT_CAPTURE = registerTag(Registries.ENTITY_TYPE, "cannot_capture");
		public static final TagKey<EntityType<?>> AFFECTED_BY_FRIENDLY = registerTag(Registries.ENTITY_TYPE, "affected_by_friendly");
		public static final TagKey<EntityType<?>> PULLED_IN_BY_MAGNETIC = registerTag(Registries.ENTITY_TYPE, "pulled_in_by_magnetic");
	}

	public static class Dimensions
	{
		public static final TagKey<DimensionType> DEPTH_DWELLER_EFFECTIVE = registerTag(Registries.DIMENSION_TYPE, "depth_dweller_effective");
		public static final TagKey<DimensionType> WAYFINDING_POINTS_TO_ORIGIN = registerTag(Registries.DIMENSION_TYPE, "wayfinding_points_to_origin");
	}

	public static class Properties
	{
		public static final TagKey<Property> DISABLES_BLOCK_ATTACK_IN_CREATIVE = registerTag(AlchemancyProperties.REGISTRY_KEY, "disables_block_attack_in_creative");
		public static final TagKey<Property> SLOTLESS = registerTag(AlchemancyProperties.REGISTRY_KEY, "slotless");
		public static final TagKey<Property> DISABLED = registerTag(AlchemancyProperties.REGISTRY_KEY, "disabled");
		public static final TagKey<Property> CODEX_UNOBTAINABLE = registerTag(AlchemancyProperties.REGISTRY_KEY, "codex_unobtainable");
		public static final TagKey<Property> CODEX_HIDDEN = registerTag(AlchemancyProperties.REGISTRY_KEY, "codex_hidden");

		public static final TagKey<Property> RETAINED_BY_UNSHAPED_CLAY = registerTag(AlchemancyProperties.REGISTRY_KEY, "retained_by_unshaped_clay");
		public static final TagKey<Property> DISABLES_SPARKLING = registerTag(AlchemancyProperties.REGISTRY_KEY, "disables_sparkling");
		public static final TagKey<Property> AFFECTED_BY_MAGNETIC = registerTag(AlchemancyProperties.REGISTRY_KEY, "affected_by_magnetic");
		public static final TagKey<Property> AFFECTED_BY_DIVINE_CLEANSE = registerTag(AlchemancyProperties.REGISTRY_KEY, "affected_by_divine_cleanse");
		public static final TagKey<Property> CHANGES_GUST_JET_WIND_COLOR = registerTag(AlchemancyProperties.REGISTRY_KEY, "changes_gust_jet_wind_color");
		public static final TagKey<Property> PREVENTS_ENDERMAN_AGGRO = registerTag(AlchemancyProperties.REGISTRY_KEY, "prevents_enderman_aggro");
		public static final TagKey<Property> IGNORED_BY_INFUSION_FLASK = registerTag(AlchemancyProperties.REGISTRY_KEY, "ignored_by_infusion_flask");
	}

	public static class DataComponents
	{
		public static final TagKey<DataComponentType<?>> DISABLES_COMPACT = registerTag(Registries.DATA_COMPONENT_TYPE, "disables_compact");
		public static final TagKey<DataComponentType<?>> UNTOGGLEABLE = registerTag(Registries.DATA_COMPONENT_TYPE, "untoggleable");
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
