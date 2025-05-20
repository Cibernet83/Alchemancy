package net.cibernet.alchemancy.properties;

import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyParticles;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SparklingProperty extends Property implements IDataHolder<Holder<Property>> {

	private static final HashMap<Holder<Property>, Supplier<ParticleOptions>> PARTICLE_MAP = new HashMap<>() {{
		put(AlchemancyProperties.BURNING, () -> ParticleTypes.FLAME);
		put(AlchemancyProperties.SPARKING, () -> ParticleTypes.SMALL_FLAME);
		put(AlchemancyProperties.FROSTED, () -> ParticleTypes.SNOWFLAKE);
		put(AlchemancyProperties.SCULKING, () -> ParticleTypes.SCULK_CHARGE_POP);
		put(AlchemancyProperties.SOULBIND, () -> ParticleTypes.SOUL);
		put(AlchemancyProperties.SPIRIT_BOND, () -> ParticleTypes.SOUL_FIRE_FLAME);
		put(AlchemancyProperties.VENGEFUL, () -> ParticleTypes.ANGRY_VILLAGER);
		put(AlchemancyProperties.WEALTHY, () -> ParticleTypes.HAPPY_VILLAGER);
		put(AlchemancyProperties.UNDYING, () -> ParticleTypes.TOTEM_OF_UNDYING);
		put(AlchemancyProperties.WARPED, () -> ParticleTypes.WARPED_SPORE);
		put(AlchemancyProperties.HELLBENT, () -> ParticleTypes.CRIMSON_SPORE);
		put(AlchemancyProperties.SHARP, () -> ParticleTypes.CRIT);
		put(AlchemancyProperties.ENCHANTING, () -> ParticleTypes.ENCHANTED_HIT);
		put(AlchemancyProperties.WISE, () -> ParticleTypes.ENCHANT);
		put(AlchemancyProperties.ARCANE, () -> ParticleTypes.DRAGON_BREATH);
		put(AlchemancyProperties.WET, () -> ParticleTypes.SPLASH);
		put(AlchemancyProperties.MYCELLIC, () -> ParticleTypes.SPORE_BLOSSOM_AIR);
		put(AlchemancyProperties.SHOCKING, () -> ParticleTypes.ELECTRIC_SPARK);
		put(AlchemancyProperties.ALLERGIC, () -> ParticleTypes.SNEEZE);
		put(AlchemancyProperties.ENDER, () -> ParticleTypes.PORTAL);
		put(AlchemancyProperties.SPORADIC, () -> ParticleTypes.MYCELIUM);
		put(AlchemancyProperties.BLINDING, () -> ParticleTypes.SQUID_INK);
		put(AlchemancyProperties.DEXTEROUS, () -> ParticleTypes.GLOW_SQUID_INK);
		put(AlchemancyProperties.SENSITIVE, () -> DustColorTransitionOptions.SCULK_TO_REDSTONE);
		put(AlchemancyProperties.ENERGIZED, () -> DustParticleOptions.REDSTONE);
		put(AlchemancyProperties.COMPACT, () -> ParticleTypes.OMINOUS_SPAWNING);
		put(AlchemancyProperties.OMINOUS, () -> ParticleTypes.TRIAL_OMEN);
		put(AlchemancyProperties.VAMPIRIC, () -> ParticleTypes.RAID_OMEN);
		put(AlchemancyProperties.HEARTY, () -> ParticleTypes.HEART);
		put(AlchemancyProperties.WEAK, () -> ParticleTypes.DAMAGE_INDICATOR);
		put(AlchemancyProperties.MUSICAL, () -> ParticleTypes.NOTE);
		put(AlchemancyProperties.LAUNCHING, () -> LaunchingProperty.PARTICLES);
		put(AlchemancyProperties.WAXED, () -> ParticleTypes.WAX_ON);
		put(AlchemancyProperties.MAGIC_RESISTANT, () -> ParticleTypes.DRIPPING_OBSIDIAN_TEAR);
		put(AlchemancyProperties.BOUNCY, () -> ParticleTypes.ITEM_SLIME);
		put(AlchemancyProperties.STICKY, () -> ParticleTypes.ITEM_COBWEB);
		put(AlchemancyProperties.FLAMMABLE, () -> ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
		put(AlchemancyProperties.EXPLODING, () -> ParticleTypes.EXPLOSION);
		put(AlchemancyProperties.WIND_CHARGED, () -> ParticleTypes.SMALL_GUST);
		put(AlchemancyProperties.FLOURISH, () -> ParticleTypes.CHERRY_LEAVES);

		put(AlchemancyProperties.DIRTY, () -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()));
		put(AlchemancyProperties.MALLEABLE, () -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.CLAY.defaultBlockState()));

		put(AlchemancyProperties.RANDOM, () -> PARTICLE_MAP.values().stream()
				.skip((int) (PARTICLE_MAP.size() * Math.random()))
				.findFirst().get().get());
	}};

	@Override
	public int getColor(ItemStack stack) {
		return 0xFF0088;
	}

	@Override
	public Component getDisplayText(ItemStack stack)
	{
		Component name = super.getDisplayText(stack);
		Holder<Property> storedStack = getData(stack);

		if(storedStack != null)
			return Component.translatable("property.detail", name, storedStack.value().getName()).withColor(getColor(stack));
		return name;
	}

	@Override
	public boolean onInfusedByDormantProperty(ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd) {

		for (Holder<Property> infusedProperty : InfusedPropertiesHelper.getInfusedProperties(propertySource)) {
			if (PARTICLE_MAP.containsKey(infusedProperty)) {
				setData(stack, infusedProperty);
				return true;
			}
		}

		return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd);
	}

	@Override
	public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {

		if (user.getRandom().nextFloat() > 0.25f ||
				InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_SPARKLING) ||
				!slot.isArmor() && getEquipmentSlotForItem(stack).isArmor()) return;

		var particle = getParticles(stack).orElse(ParticleTypes.END_ROD);

		double y = (!slot.isArmor() || slot == EquipmentSlot.BODY) ? user.getRandomY() : user.getY((user.getRandom().nextDouble() + slot.getIndex()) * 0.25);
		user.level().addParticle(particle, user.getRandomX(1), y, user.getRandomZ(1), 0, 0, 0);
	}

	@Override
	public void onProjectileTick(ItemStack stack, Projectile projectile) {

		var particle = getParticles(stack).orElse(ParticleTypes.END_ROD);
		projectile.level().addParticle(particle, projectile.getX(), projectile.getY(), projectile.getZ(), 0, 0, 0);
	}

	@Override
	public void onEntityItemTick(ItemStack stack, ItemEntity itemEntity) {

		if (itemEntity.getRandom().nextFloat() > 0.25f ||
				InfusedPropertiesHelper.hasProperty(stack, AlchemancyTags.Properties.DISABLES_SPARKLING)) return;
		var particle = getParticles(stack).orElse(ParticleTypes.END_ROD);
		itemEntity.level().addParticle(particle, itemEntity.getRandomX(1), itemEntity.getRandomY(), itemEntity.getRandomZ(1), 0, 0, 0);
	}

	@Override
	public void onRootedAnimateTick(RootedItemBlockEntity root, RandomSource randomSource) {
		playRootedParticles(root, randomSource, getParticles(root.getItem()).orElse(ParticleTypes.END_ROD));
	}

	public static Optional<ParticleOptions> getParticles(ItemStack stack) {
		if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SPARKLING))
			return Optional.empty();


		Holder<Property> result;
		if(PropertyModifierComponent.getOrElse(stack, AlchemancyProperties.SPARKLING, AlchemancyProperties.Modifiers.IGNORE_INFUSED, true))
			result = AlchemancyProperties.SPARKLING.get().getData(stack);
		else {
			var infusions = InfusedPropertiesHelper.getInfusedProperties(stack);
			result = infusions.isEmpty() ? null : infusions.getFirst();
		}

		return result == null || !PARTICLE_MAP.containsKey(result) ? Optional.empty() : Optional.of(PARTICLE_MAP.get(result).get());
	}

	@Override
	public Holder<Property> readData(CompoundTag tag) {

		if (tag.contains("stored_property"))
			return Property.CODEC.parse(CommonUtils.registryAccessStatic().createSerializationContext(NbtOps.INSTANCE), tag.get("stored_property")).getOrThrow();
		return getDefaultData();
	}

	@Override
	public CompoundTag writeData(Holder<Property> data) {
		return new CompoundTag() {{
			if (data != null)
				put("stored_property", Property.CODEC.encodeStart(CommonUtils.registryAccessStatic().createSerializationContext(NbtOps.INSTANCE), data).getOrThrow());
		}};
	}

	@Override
	public Holder<Property> getDefaultData() {
		return null;
	}
}
