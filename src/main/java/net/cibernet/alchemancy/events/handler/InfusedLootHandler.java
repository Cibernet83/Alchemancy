package net.cibernet.alchemancy.events.handler;

import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.*;

@EventBusSubscriber
public class InfusedLootHandler
{
	public static final FeatureFlag FEATURE_FLAG = FeatureFlags.REGISTRY.getFlag(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "infused_loot"));

	private static final TagKey<Item> PROVIDES_ARROW = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "provides_arrow"));

	@SubscribeEvent
	private static void finalizeSpawn(final FinalizeSpawnEvent event)
	{
		if(event.getLevel().enabledFeatures().contains(FEATURE_FLAG) && event.getLevel().getRandom().nextFloat() < 0.01f)
			markForInfusions(event.getEntity());
	}

	@SubscribeEvent
	private static void onEntityTick(final EntityTickEvent.Pre event)
	{
		Level level = event.getEntity().level();
		if(!level.isClientSide() && level.enabledFeatures().contains(FEATURE_FLAG) && isMarkedForInfusions(event.getEntity()) && event.getEntity() instanceof Mob entity)
		{
			List<EquipmentSlot> slots = Arrays.asList(EquipmentSlot.values());
			Collections.shuffle(slots);

			for (EquipmentSlot slot : slots) {
				ItemStack stack = entity.getItemBySlot(slot);

				if (slot == EquipmentSlot.OFFHAND && stack.isEmpty() && entity.getMainHandItem().is(PROVIDES_ARROW)) {
					stack = Items.ARROW.getDefaultInstance();
					stack.setCount(entity.getRandom().nextInt(8) + 1);
				}

				if (stack.isEmpty())
					continue;

				RandomSource randomSource = entity.getRandom();
				stack = applyRandomProperties(level, stack, slot.getName(), randomSource);

				if (!stack.isEmpty())
				{
					entity.setDropChance(slot, 1);
					entity.setItemSlot(slot, stack);
				}
				break;

			}

			resolveMarkForInfusions(entity);
		}
	}

	public static void infuseRandomItems(Level level, float luck, List<ItemStack> pool)
	{
		if(!level.enabledFeatures().contains(FEATURE_FLAG) && level.getRandom().nextFloat() < 0.02f)
			return;

		RandomSource randomSource = level.getRandom();
		ArrayList<ItemStack> poolCopy = new ArrayList<>(pool);
		Collections.shuffle(poolCopy);

		Optional<ItemStack> tool = poolCopy.stream().filter(ItemStack::isDamageableItem).findAny();
		tool.ifPresent(stack -> applyRandomProperties(level, stack, Property.getEquipmentSlotForItem(stack).getName(), randomSource));
		poolCopy.removeIf(ItemStack::isDamageableItem);

		if(randomSource.nextBoolean())
			for(int i = 0; i < randomSource.nextInt(8) + luck && i < poolCopy.size(); i++)
			{
				ItemStack stack = poolCopy.get(i);
				applyRandomProperties(level, stack, stack.has(DataComponents.FOOD) ? "food" : "material", randomSource);
			}
	}

	private static ItemStack applyRandomProperties(Level level, ItemStack stack, String tagKey, RandomSource randomSource)
	{
		boolean success = false;
		for (int i = 0; i < 3 && randomSource.nextBoolean(); i++) {
			if (applyRandomProperty(level, stack, tagKey, randomSource, false))
				success = true;
			else break;
		}

		if (randomSource.nextFloat() < 0.05f) {
			applyRandomProperty(level, stack, tagKey, randomSource, true);
			success = true;
		}

		if(success)
		{
			ItemStack newStack = ForgeRecipeGrid.resolveInteractions(stack, level);
			stack.set(AlchemancyItems.Components.INFUSED_PROPERTIES, newStack.get(AlchemancyItems.Components.INFUSED_PROPERTIES));
			stack.set(AlchemancyItems.Components.PROPERTY_DATA, newStack.get(AlchemancyItems.Components.PROPERTY_DATA));

			if(!InfusedPropertiesHelper.getInfusedProperties(stack).isEmpty())
				return newStack;
		}

		return ItemStack.EMPTY;
	}

	private static boolean applyRandomProperty(Level level, ItemStack stack, String tagKey, RandomSource randomSource, boolean isRare)
	{
		Optional<HolderSet.Named<Property>> validProperties = level.registryAccess().lookupOrThrow(AlchemancyProperties.REGISTRY_KEY).get(getLootTag(tagKey, isRare));
		if(validProperties.isEmpty() || validProperties.get().stream().findAny().isEmpty())
			return false;
		Optional<Holder<Property>> property = validProperties.get().getRandomElement(randomSource);
		if(property.isEmpty())
			return false;
		InfusedPropertiesHelper.addProperty(stack, property.get());

		return true;
	}

	private static TagKey<Property> getLootTag(String tagKey, boolean isRare)
	{
		return TagKey.create(AlchemancyProperties.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "loot_infusions/" + (isRare ? "rare/" : "") + tagKey));
	}

	private static void markForInfusions(Entity target)
	{
		target.getPersistentData().putBoolean(Alchemancy.MODID + ":marked_for_infusions", true);
	}

	private static void resolveMarkForInfusions(Entity target)
	{
		target.getPersistentData().putBoolean(Alchemancy.MODID + ":marked_for_infusions", false);
	}

	private static boolean isMarkedForInfusions(Entity target)
	{
		return target.getPersistentData().getBoolean(Alchemancy.MODID + ":marked_for_infusions");
	}

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public static class ModBus
	{
		@SubscribeEvent
		private static void addFeaturePacks(final AddPackFindersEvent event)
		{
			//FinalizeSpawnEvent
			event.addPackFinders(ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "data/alchemancy/datapacks/infused_loot"), PackType.SERVER_DATA, Component.translatable("dataPack.alchemancy.infused_loot"), PackSource.FEATURE, false, Pack.Position.TOP);
		}
	}
}
