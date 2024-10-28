package net.cibernet.alchemancy.registries;

import com.mojang.serialization.Codec;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.*;
import net.cibernet.alchemancy.properties.data.modifiers.PropertyModifierType;
import net.cibernet.alchemancy.properties.soulbind.EnergySapperProperty;
import net.cibernet.alchemancy.properties.soulbind.SoulbindProperty;
import net.cibernet.alchemancy.properties.soulbind.VengefulProperty;
import net.cibernet.alchemancy.properties.special.*;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlchemancyProperties
{
	private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "properties");
	public static final DeferredRegister<Property> REGISTRY = DeferredRegister.create(KEY, Alchemancy.MODID);
	private static final Registry<Property> SUPPLIER = REGISTRY.makeRegistry(propertyRegistryBuilder -> propertyRegistryBuilder.defaultKey(KEY).sync(true));


	public static class Modifiers
	{
		private static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "property_modifiers");
		public static final DeferredRegister<PropertyModifierType<?>> REGISTRY = DeferredRegister.create(KEY, Alchemancy.MODID);
		private static final Registry<PropertyModifierType<?>> SUPPLIER = REGISTRY.makeRegistry(propertyRegistryBuilder -> propertyRegistryBuilder.defaultKey(KEY).sync(true));

		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Boolean>> PREVENT_CONSUMPTION = REGISTRY.register("prevent_consumption", PropertyModifierType.build(false, Codec.BOOL, ByteBufCodecs.BOOL));
		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Integer>> DURABILITY_CONSUMPTION = REGISTRY.register("durability_consumption", PropertyModifierType.build(1, Codec.INT, ByteBufCodecs.INT));
		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> ATTACK_DAMAGE = REGISTRY.register("attack_damage", PropertyModifierType.build(1f, Codec.FLOAT, ByteBufCodecs.FLOAT));
		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> ATTACK_RADIUS = REGISTRY.register("attack_radius", PropertyModifierType.build(1f, Codec.FLOAT, ByteBufCodecs.FLOAT));
		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> EFFECT_RADIUS = REGISTRY.register("effect_radius", PropertyModifierType.build(1f, Codec.FLOAT, ByteBufCodecs.FLOAT));
		public static final DeferredHolder<PropertyModifierType<?>, PropertyModifierType<Float>> EFFECT_VALUE = REGISTRY.register("effect_value", PropertyModifierType.build(1f, Codec.FLOAT, ByteBufCodecs.FLOAT));

		public static Holder<PropertyModifierType<?>> asHolder(PropertyModifierType<?> modifierType)
		{

			return SUPPLIER.asLookup().get(ResourceKey.create(AlchemancyProperties.Modifiers.REGISTRY.getRegistryKey(), getKey(modifierType))).orElse(null);
		}

		public static ResourceLocation getKey(PropertyModifierType<?> modifierType)
		{
			return AlchemancyProperties.Modifiers.SUPPLIER.getKey(modifierType);
		}
	}

	//Elemental
	public static final DeferredHolder<Property, BurningProperty> BURNING = REGISTRY.register("burning", BurningProperty::new);
	public static final DeferredHolder<Property, WetProperty> WET = REGISTRY.register("wet", WetProperty::new);
	public static final DeferredHolder<Property, FrostedProperty> FROSTED = REGISTRY.register("frosted", FrostedProperty::new);
	public static final DeferredHolder<Property, ShockDamageProperty> SHOCKING = REGISTRY.register("shocking", ShockDamageProperty::new);
	public static final DeferredHolder<Property, PhotosyntheticProperty> PHOTOSYNTHETIC = REGISTRY.register("photosynthetic", PhotosyntheticProperty::new);

	//Materials
	public static final DeferredHolder<Property, FlammableProperty> FLAMMABLE = REGISTRY.register("flammable", FlammableProperty::new);
	public static final DeferredHolder<Property, Property> CHARRED = REGISTRY.register("charred", () -> Property.simple(0x444242));
	public static final DeferredHolder<Property, DurabilityMultiplierProperty> STURDY = REGISTRY.register("sturdy", () -> new DurabilityMultiplierProperty(0x9A9A9A, 1.2f));
	public static final DeferredHolder<Property, BrittleProperty> BRITTLE = REGISTRY.register("brittle", BrittleProperty::new);
	public static final DeferredHolder<Property, RustyProperty> RUSTY = REGISTRY.register("rusty", RustyProperty::new);
	public static final DeferredHolder<Property, FerrousProperty> FERROUS = REGISTRY.register("ferrous", FerrousProperty::new);
	public static final DeferredHolder<Property, GildedProperty> GILDED = REGISTRY.register("gilded", GildedProperty::new);
	public static final DeferredHolder<Property, ImprovedProperty> LUSTROUS = REGISTRY.register("lustrous", ImprovedProperty::new);
	public static final DeferredHolder<Property, WealthyProperty> WEALTHY = REGISTRY.register("wealthy", WealthyProperty::new);
	public static final DeferredHolder<Property, ReinforcedProperty> REINFORCED = REGISTRY.register("reinforced", ReinforcedProperty::new);
	public static final DeferredHolder<Property, PristineProperty> PRISTINE = REGISTRY.register("pristine", PristineProperty::new);
	public static final DeferredHolder<Property, HellbentProperty> HELLBENT = REGISTRY.register("hellbent", HellbentProperty::new);
	public static final DeferredHolder<Property, DepthDwellerProperty> DEPTH_DWELLER = REGISTRY.register("depth_dweller", DepthDwellerProperty::new);

	//Mobility
	public static final DeferredHolder<Property, EnergizedProperty> ENERGIZED = REGISTRY.register("energized", EnergizedProperty::new);
	public static final DeferredHolder<Property, BouncyProperty> BOUNCY = REGISTRY.register("bouncy", BouncyProperty::new);
	public static final DeferredHolder<Property, StickyProperty> STICKY = REGISTRY.register("sticky", StickyProperty::new);
	public static final DeferredHolder<Property, SlipperyProperty> SLIPPERY = REGISTRY.register("slippery", SlipperyProperty::new);
	public static final DeferredHolder<Property, BuoyantProperty> BUOYANT = REGISTRY.register("buoyant", BuoyantProperty::new);
	public static final DeferredHolder<Property, LightweightProperty> LIGHTWEIGHT = REGISTRY.register("lightweight", LightweightProperty::new);
	public static final DeferredHolder<Property, HeavyProperty> HEAVY = REGISTRY.register("heavy", HeavyProperty::new);
	public static final DeferredHolder<Property, AntigravProperty> ANTIGRAV = REGISTRY.register("antigrav", AntigravProperty::new);

	//Tools
	public static final DeferredHolder<Property, ToolProperty> MINING = REGISTRY.register("mining", () -> new ToolProperty(0x888788, BlockTags.MINEABLE_WITH_PICKAXE, ItemAbilities.DEFAULT_PICKAXE_ACTIONS));
	public static final DeferredHolder<Property, ToolProperty> CHOPPING = REGISTRY.register("chopping", () -> new ToolProperty(0x917142, BlockTags.MINEABLE_WITH_AXE, ItemAbilities.DEFAULT_AXE_ACTIONS));
	public static final DeferredHolder<Property, ToolProperty> DIGGING = REGISTRY.register("digging", () -> new ToolProperty(0xB9855C, BlockTags.MINEABLE_WITH_SHOVEL, ItemAbilities.DEFAULT_SHOVEL_ACTIONS));
	public static final DeferredHolder<Property, ToolProperty> REAPING = REGISTRY.register("reaping", () -> new ToolProperty(0x57AD3F, BlockTags.MINEABLE_WITH_HOE, ItemAbilities.DEFAULT_HOE_ACTIONS));
	public static final DeferredHolder<Property, ShearingProperty> SHEARING = REGISTRY.register("shearing", () -> new ShearingProperty(0xA64F3F, ToolProperty.getShearsRules(), ItemAbilities.DEFAULT_SHEARS_ACTIONS));
	public static final DeferredHolder<Property, ToolProperty> SLASHING = REGISTRY.register("slashing", () -> new ToolProperty(0xDAD1C5, ToolProperty.getSwordRules(), ItemAbilities.DEFAULT_SWORD_ACTIONS));
	public static final DeferredHolder<Property, BowProperty> SHARPSHOOTING = REGISTRY.register("sharpshooting", BowProperty::new);
	public static final DeferredHolder<Property, ShieldingProperty> SHIELDING = REGISTRY.register("shielding", ShieldingProperty::new);
	public static final DeferredHolder<Property, FirestarterProperty> FIRESTARTING = REGISTRY.register("firestarting", () -> new FirestarterProperty(0xFFB051, List.of(), ItemAbilities.DEFAULT_FLINT_ACTIONS));
	public static final DeferredHolder<Property, Property> SCOPING = REGISTRY.register("scoping", () -> Property.simple(0xDE923A));
	public static final DeferredHolder<Property, HeadearProperty> HEADWEAR = REGISTRY.register("headwear", HeadearProperty::new);
	public static final DeferredHolder<Property, SaddledProperty> SADDLED = REGISTRY.register("saddled", SaddledProperty::new);
	public static final DeferredHolder<Property, GliderProperty> GLIDER = REGISTRY.register("glider", GliderProperty::new);

	public static final DeferredHolder<Property, CraftyProperty> CRAFTY = REGISTRY.register("crafty", CraftyProperty::new);
	public static final DeferredHolder<Property, AutosmeltProperty> SMELTING = REGISTRY.register("smelting", AutosmeltProperty::new);
	public static final DeferredHolder<Property, AssembleProperty> ASSEMBLING = REGISTRY.register("assembling", AssembleProperty::new);
	public static final DeferredHolder<Property, AssimilatingProperty> ASSIMILATING = REGISTRY.register("assimilating", AssimilatingProperty::new);

	//Storage
	public static final DeferredHolder<Property, HollowProperty> HOLLOW = REGISTRY.register("hollow", HollowProperty::new);
	public static final DeferredHolder<Property, BucketingProperty> BUCKETING = REGISTRY.register("bucketing", BucketingProperty::new);
	public static final DeferredHolder<Property, EncapsulatingProperty> ENCAPSULATING = REGISTRY.register("encapsulating", EncapsulatingProperty::new);
	public static final DeferredHolder<Property, CapturingProperty> CAPTURING = REGISTRY.register("capturing", CapturingProperty::new);
	public static final DeferredHolder<Property, DrippingProperty> DRIPPING = REGISTRY.register("dripping", DrippingProperty::new);

	//Triggering
	public static final DeferredHolder<Property, EdibleProperty> EDIBLE = REGISTRY.register("edible", EdibleProperty::new);
	public static final DeferredHolder<Property, JaggedProperty> JAGGED = REGISTRY.register("jagged", JaggedProperty::new);
	public static final DeferredHolder<Property, RootedProperty> ROOTED = REGISTRY.register("rooted", RootedProperty::new);
	public static final DeferredHolder<Property, SensitiveProperty> SENSITIVE = REGISTRY.register("sensitive", SensitiveProperty::new);
	public static final DeferredHolder<Property, InteractableProperty> INTERACTABLE = REGISTRY.register("interactable", InteractableProperty::new);
	public static final DeferredHolder<Property, SporeCloudProperty> MYCELLIC = REGISTRY.register("mycellic", SporeCloudProperty::new);
	public static final DeferredHolder<Property, SporadicProperty> SPORADIC = REGISTRY.register("sporadic", SporadicProperty::new);
	public static final DeferredHolder<Property, ShatteringProperty> SHATTERING = REGISTRY.register("shattering", ShatteringProperty::new);
	public static final DeferredHolder<Property, ThrowableProperty> THROWABLE = REGISTRY.register("throwable", ThrowableProperty::new);
	public static final DeferredHolder<Property, ToggleableProperty> TOGGLEABLE = REGISTRY.register("toggleable", ToggleableProperty::new);
	public static final DeferredHolder<Property, TickingProperty> TICKING = REGISTRY.register("ticking", TickingProperty::new);

	//Mob Effects
	public static final DeferredHolder<Property, LevitatingProperty> LEVITATING = REGISTRY.register("levitating", LevitatingProperty::new);
	public static final DeferredHolder<Property, SwiftProperty> SWIFT = REGISTRY.register("swift", SwiftProperty::new);
	public static final DeferredHolder<Property, SluggishProperty> SLUGGISH = REGISTRY.register("sluggish", SluggishProperty::new);
	public static final DeferredHolder<Property, MobEffectOnHitProperty> POISONOUS = REGISTRY.register("poisonous", () -> new MobEffectOnHitProperty(new MobEffectInstance(MobEffects.POISON, 100)));
	public static final DeferredHolder<Property, DecayingProperty> DECAYING = REGISTRY.register("decaying", DecayingProperty::new);
	public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> TIPSY = REGISTRY.register("tipsy", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(MobEffects.CONFUSION, 200, 1), EquipmentSlotGroup.ANY, true));
	public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> BLINDING = REGISTRY.register("blinding", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0), EquipmentSlotGroup.HEAD, false));
	public static final DeferredHolder<Property, NocturnalProperty> NOCTURNAL = REGISTRY.register("nocturnal", NocturnalProperty::new);
	public static final DeferredHolder<Property, DivingGearProperty> AQUATIC = REGISTRY.register("aquatic", DivingGearProperty::new);
	public static final DeferredHolder<Property, LeapingProperty> LEAPING = REGISTRY.register("leaping", LeapingProperty::new);
	public static final DeferredHolder<Property, GlowingProperty> GLOWING_AURA = REGISTRY.register("glowing_aura", GlowingProperty::new);
	public static final DeferredHolder<Property, MobEffectEquippedAndHitProperty> OMINOUS = REGISTRY.register("ominous", () -> new MobEffectEquippedAndHitProperty(new MobEffectInstance(MobEffects.BAD_OMEN, 10), EquipmentSlotGroup.ANY, false));

	//Offensive
	public static final DeferredHolder<Property, GrapplingProperty> GRAPPLING = REGISTRY.register("grappling", GrapplingProperty::new);
	public static final DeferredHolder<Property, SpikingProperty> SPIKING = REGISTRY.register("spiking", SpikingProperty::new);
	public static final DeferredHolder<Property, DamageMultiplierProperty> SHARP = REGISTRY.register("sharp", () -> new DamageMultiplierProperty(0xEAE5DE, 1.2f));
	public static final DeferredHolder<Property, WeakProperty> WEAK = REGISTRY.register("weak", WeakProperty::new);
	public static final DeferredHolder<Property, DenseProperty> DENSE = REGISTRY.register("dense", DenseProperty::new);
	public static final DeferredHolder<Property, LetsGoGamblingProperty> GAMBLING = REGISTRY.register("gambling", LetsGoGamblingProperty::new);
	public static final DeferredHolder<Property, ArcaneProperty> ARCANE = REGISTRY.register("arcane", ArcaneProperty::new);
	public static final DeferredHolder<Property, ResizedProperty> RESIZED = REGISTRY.register("resized", ResizedProperty::new);

	//On Crit
	public static final DeferredHolder<Property, ExplodingProperty> EXPLODING = REGISTRY.register("exploding", () -> new ExplodingProperty(0xDB2F1A, 3, 5, ExplodingProperty.destroyBlocks()));
	public static final DeferredHolder<Property, ExplodingProperty> WIND_CHARGED = REGISTRY.register("wind_charged", () -> new ExplodingProperty(MobEffects.WIND_CHARGED.value().getColor(), 3, 5, ExplodingProperty.gust()));
	public static final DeferredHolder<Property, LightningBoltProperty> SMITING = REGISTRY.register("smiting", LightningBoltProperty::new);

	//Resistances
	public static final DeferredHolder<Property, CozyProperty> COZY = REGISTRY.register("cozy", CozyProperty::new);
	public static final DeferredHolder<Property, WaxedProperty> WAXED = REGISTRY.register("waxed", WaxedProperty::new);
	public static final DeferredHolder<Property, FireproofProperty> FIRE_RESISTANT = REGISTRY.register("fire_resistant", FireproofProperty::new);
	public static final DeferredHolder<Property, ConditionalDamageReductionProperty> BLAST_RESISTANT = REGISTRY.register("blast_resistant", () -> ConditionalDamageReductionProperty.reduceExplosionDamage(0x3B2754)); //2CCC26
	public static final DeferredHolder<Property, ConditionalDamageReductionProperty> INSULATED = REGISTRY.register("insulated", () -> ConditionalDamageReductionProperty.reduceShockDamage(0x659191));
	public static final DeferredHolder<Property, WardingProperty> WARDING = REGISTRY.register("warding", WardingProperty::new);
	public static final DeferredHolder<Property, EternalProperty> ETERNAL = REGISTRY.register("eternal", EternalProperty::new);

	//Soulbind
	public static final DeferredHolder<Property, Property> SOULBIND = REGISTRY.register("soulbind", SoulbindProperty::new);
	public static final DeferredHolder<Property, LoyalProperty> LOYAL = REGISTRY.register("loyal", LoyalProperty::new);
	public static final DeferredHolder<Property, VengefulProperty> VENGEFUL = REGISTRY.register("vengeful", VengefulProperty::new);
	public static final DeferredHolder<Property, VampiricProperty> VAMPIRIC = REGISTRY.register("vampiric", VampiricProperty::new);
	public static final DeferredHolder<Property, EnergySapperProperty> ENERGY_SAPPER = REGISTRY.register("energy_sapper", EnergySapperProperty::new);
	public static final DeferredHolder<Property, RelentlessProperty> RELENTLESS = REGISTRY.register("relentless", RelentlessProperty::new);
	public static final DeferredHolder<Property, SpiritBondProperty> SPIRIT_BOND = REGISTRY.register("spirit_bond", SpiritBondProperty::new);
	public static final DeferredHolder<Property, PhasingProperty> PHASING = REGISTRY.register("phasing", PhasingProperty::new);

	//Misc
	public static final DeferredHolder<Property, NonlethalProperty> NONLETHAL = REGISTRY.register("nonlethal", NonlethalProperty::new);
	public static final DeferredHolder<Property, MendingProperty> MENDING = REGISTRY.register("mending", MendingProperty::new);
	public static final DeferredHolder<Property, FlourishingProperty> FLOURISH = REGISTRY.register("flourish", FlourishingProperty::new);
	public static final DeferredHolder<Property, UndeadProperty> UNDEAD = REGISTRY.register("undead", UndeadProperty::new);
	public static final DeferredHolder<Property, UndyingProperty> UNDYING = REGISTRY.register("undying", UndyingProperty::new);

	public static final DeferredHolder<Property, InfectedProperty> INFECTED = REGISTRY.register("infected", InfectedProperty::new);
	public static final DeferredHolder<Property, DeadProperty> DEAD = REGISTRY.register("dead", DeadProperty::new);
	public static final DeferredHolder<Property, Property> SANITIZED = REGISTRY.register("sanitized", () -> Property.simple(0xBFFFF4));

	public static final DeferredHolder<Property, SweetProperty> SWEET = REGISTRY.register("sweet", SweetProperty::new);
	public static final DeferredHolder<Property, Property> SCARY = REGISTRY.register("scary", () -> Property.simple(0x7A2000));
	public static final DeferredHolder<Property, Property> SEEDED = REGISTRY.register("seeded", () -> SpreadsOnHitProperty.simple(0xACB038, EquipmentSlotGroup.ARMOR));

	public static final DeferredHolder<Property, ConductiveProperty> CONDUCTIVE = REGISTRY.register("conductive", ConductiveProperty::new);
	public static final DeferredHolder<Property, ExperienceBoostProperty> WISE = REGISTRY.register("wise", ExperienceBoostProperty::new);
	public static final DeferredHolder<Property, ExperiencedProperty> EXPERIENCED = REGISTRY.register("experienced", ExperiencedProperty::new);
	public static final DeferredHolder<Property, EnderProperty> ENDER = REGISTRY.register("ender", EnderProperty::new);
	public static final DeferredHolder<Property, SculkingProperty> SCULKING = REGISTRY.register("sculking", SculkingProperty::new);
	public static final DeferredHolder<Property, LooseProperty> LOOSE = REGISTRY.register("loose", LooseProperty::new);
	public static final DeferredHolder<Property, SparkingProperty> SPARKING = REGISTRY.register("sparking", SparkingProperty::new);
	public static final DeferredHolder<Property, ExtendedProperty> EXTENDED = REGISTRY.register("extended", ExtendedProperty::new);
	public static final DeferredHolder<Property, CalciumProperty> CALCAREOUS = REGISTRY.register("calcareous", CalciumProperty::new);
	public static final DeferredHolder<Property, MusicalProperty> MUSICAL = REGISTRY.register("musical", MusicalProperty::new);
	public static final DeferredHolder<Property, EntityPullProperty<Projectile>> TARGETED = REGISTRY.register("targeted", () -> new EntityPullProperty<>(0xDC4A4A, Projectile.class, 12, false));

	//Cosmetic
	public static final DeferredHolder<Property, Property> REVEALED = REGISTRY.register("revealed", () -> Property.simple(0xD6DDFF));
	public static final DeferredHolder<Property, Property> REVEALING = REGISTRY.register("revealing", () -> Property.simple((style) -> style.withBold(true), () -> 0xD6DDFF));
	public static final DeferredHolder<Property, Property> SCRAMBLED = REGISTRY.register("scrambled", () -> Property.simple(0x292200));
	public static final DeferredHolder<Property, Property> CONCEALED = REGISTRY.register("concealed", () -> Property.simple(0x605665));
	public static final DeferredHolder<Property, DisguisedProperty> DISGUISED = REGISTRY.register("disguised", DisguisedProperty::new);
	public static final DeferredHolder<Property, SeethroughProperty> SEETHROUGH = REGISTRY.register("seethrough", SeethroughProperty::new);
	public static final DeferredHolder<Property, TintedProperty> TINTED = REGISTRY.register("tinted", TintedProperty::new);


	//Special
	public static final DeferredHolder<Property, Property> AWAKENED = REGISTRY.register("awakened", () -> Property.simpleInterpolated(false, 0.5f, 0x91EAE3, 0x91EAE3, 0xEDF2F8, 0xEBBBDB, 0xEBBBDB, 0xEDF2F8));
	public static final DeferredHolder<Property, Property> PARADOXICAL = REGISTRY.register("paradoxical", () -> Property.simpleFlashing(true, 100d, 0xFF0000, 0x00FF00, 0x0000FF, 0xA100FF));
	public static final DeferredHolder<Property, Property> LIMIT_BREAK = REGISTRY.register("limit_break", () -> IncreaseInfuseSlotsProperty.simple(2, (style) -> style.withBold(true), () -> ColorUtils.interpolateColorsOverTime(0.1f, 0xFF9D14, 0xFFE14F, 0xFFFF9B, 0xFFFFFF)));

	public static final DeferredHolder<Property, Property> DIRTY = REGISTRY.register("dirty", () -> Property.simple(0x96592E));
	public static final DeferredHolder<Property, Property> AWKWARD = REGISTRY.register("awkward", () -> Property.simple(0xA5266C));
	public static final DeferredHolder<Property, Property> WARPED = REGISTRY.register("warped", () -> Property.simple(0x14B485));

	//Stuff to goof around
	public static final DeferredHolder<Property, RandomEffectProperty> RANDOM = REGISTRY.register("random", RandomEffectProperty::new);
	public static final DeferredHolder<Property, BlockVacuumProperty> BLOCK_VACUUM = REGISTRY.register("block_vacuum", BlockVacuumProperty::new);
	public static final DeferredHolder<Property, BigSuckProperty> CEASELESS_VOID = REGISTRY.register("ceaseless_void", BigSuckProperty::new);
	public static final DeferredHolder<Property, VoidtouchProperty> VOIDTOUCH = REGISTRY.register("voidtouch", VoidtouchProperty::new);
	public static final DeferredHolder<Property, Property> QUANTUM_BIND = REGISTRY.register("quantum_bind", () -> Property.simple(0xFFFF00));

	public static final DeferredHolder<Property, AuxiliaryProperty> AUXILIARY = REGISTRY.register("auxiliary", AuxiliaryProperty::new);
	public static final DeferredHolder<Property, GlowRingProperty> ETERNAL_GLOW = REGISTRY.register("eternal_glow", GlowRingProperty::new);
	public static final DeferredHolder<Property, PhaseRingProperty> PHASE_STEP = REGISTRY.register("phase_step", PhaseRingProperty::new);

	//TODO
	//Arcane: ??? - Dragon's Breath/Dragon Head
	//Wayfinding: ??? - Compass/Maps
	//Brushing: Copper Brush behavior - Brush
	//Seeking: Projectiles home towards Glowing entities
	//Ticking: Has a timer that starts ticking down whenever the item's in a player's inventory. Triggers On Activate when timer reaches 0. Timer resets on pick up - Clock/Repeater
	//Tethered: On Right Click leashes the user to the targeted entity or fence block. Leash/String maybe?
	//Flattened: Lets the item be placed onto walls - Painting
	//Echoing: On Hit/On Activation, the action and target are stored into the item and get repeated after a few ticks (as long as the item remains in the player's inv or as a projectile) - Echo Shard/Recovery Compass
	//Pristine: Grants extra durability in the form of Property Percentage.

	//Soulbind: Used to make Soul Properties. Has a chance to consume itself when in the player's inventory. When consumed also has a chance at attacking the user. - Soul Sand
	//Spirit Bond: Unbreaking effect when user is low on health
	//Energy Sapper: chance to consume hunger when held to repair weapon. Consumes target's hunger On Hit
	//Cursed: Reduces Luck stat when equipped
	//Vengeful: Increased damage against the last entity attacking the user. Reduced damage against everything else
	//Vampiric: chance at healing from attacks
	//Sentient: Moves around as if it were alive when dropped. Has a chance to pop off when Rooted. Has a chance to drop when in the player's inventory
	//Spiritual: Creates a weaker copy of the item when consumed.
	//Reincarnating: Item is retained upon death. One-time use.
	//Phasing: Lets dropped items and projectiles clip through blocks

	//Warped Properties:
	//Repelled: Pushes itself away from ALL nearby entities - Targeted/Loyal
	//Weak: increases damage taken/reduces armor effectiveness when worn - Warding/Reinforced
	//Sluggish: applies Slowness on hit/when equipped - Swift
	//Muffled: Suppresses certain vibrations when equipped - Musical/Sensitive/Echoing
	//Clueless: ??? - Wise/Wayfinding
	//Lucky: Increases Luck stat when equipped - ??? (don't wanna make it Warped Cursed bc then it'd be too easy to get rid of)

	@Nullable
	public static Holder<Property> getProperty(ResourceLocation key)
	{
		//going for getOptional so it doesn't crash on null
		return SUPPLIER.asLookup().get(ResourceKey.create(REGISTRY.getRegistryKey(), key)).orElse(null);
	}
	private static Holder<Property> getProperty(TagKey<Item> tag)
	{
		return getProperty(ResourceLocation.fromNamespaceAndPath(tag.location().getNamespace(), tag.location().getPath().substring(tag.location().getPath().lastIndexOf('/')+1)));
	}

	public static ResourceLocation getKeyFor(Property property)
	{
		return SUPPLIER.getKey(property);
	}

	public static List<Holder<Property>> getDormantProperties(ItemStack stack)
	{
		List<Holder<Property>> res = new ArrayList<>();

		for(TagKey<Item> tag : stack.getTags().filter(t -> t.location().getPath().contains("dormant_properties")).toList())
		{
			Holder<Property> property = getProperty(tag);
			if(property != null)
				res.add(property);
		}
		return res;
	}

	@Nullable
	public static Holder<Property> getHolder(Property property)
	{
		return SUPPLIER.asLookup().get(ResourceKey.create(REGISTRY.getRegistryKey(), property.getKey())).orElse(null);
	}
}
