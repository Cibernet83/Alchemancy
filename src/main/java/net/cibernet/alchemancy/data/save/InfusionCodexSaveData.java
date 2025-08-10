package net.cibernet.alchemancy.data.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.InfusionCodexToast;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

@EventBusSubscriber(Dist.CLIENT)
public class InfusionCodexSaveData {

	public static final String FILE_PATH = Alchemancy.MODID + "/infusion_codex_save_data.json";
	private static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private static boolean unlockEverything = false;
	private static int maxRecencyIndex = 0;
	private static int minRecencyIndex = 0;
	private static int currentUnlockIndex = 0;

	private static boolean dirty;

	private static final Map<ResourceLocation, EntryData> UNLOCKED_INFUSIONS = new HashMap<>();
	private static final List<ResourceLocation> DISCOVERED_ITEMS = new ArrayList<>();

	private static final Codec<Unit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("nerd_mode").forGetter(data -> java.util.Optional.of(unlockEverything)),
			Codec.unboundedMap(ResourceLocation.CODEC, EntryData.CODEC).fieldOf("unlocked_infusions").forGetter(data -> UNLOCKED_INFUSIONS),
			Codec.list(ResourceLocation.CODEC).fieldOf("discovered_items").forGetter(data -> DISCOVERED_ITEMS)
	).apply(instance, (nerdMode, unlockedInfusions, discoveredItems) -> {

		unlockEverything = nerdMode.orElse(false);

		UNLOCKED_INFUSIONS.clear();
		UNLOCKED_INFUSIONS.putAll(unlockedInfusions);
		DISCOVERED_ITEMS.clear();
		DISCOVERED_ITEMS.addAll(discoveredItems);

		var recencyIndexes = UNLOCKED_INFUSIONS.values().stream().map(data -> data.recencyIndex).filter(i -> i > 0).sorted().toList();
		if(!recencyIndexes.isEmpty())
		{
			minRecencyIndex = recencyIndexes.getFirst();
			maxRecencyIndex = recencyIndexes.getLast();
		}

		UNLOCKED_INFUSIONS.values().stream().map(data -> data.unlockIndex).filter(i -> i > 0).sorted(Comparator.comparingInt(i -> -i)).findFirst().ifPresent((i) -> currentUnlockIndex = Math.max(0, i));

		return Unit.INSTANCE;
	}));

	private static void save() {

		JsonElement json = CODEC.encodeStart(JsonOps.INSTANCE, Unit.INSTANCE).getOrThrow();
		new File(Alchemancy.MODID).mkdirs();
		try (Writer writer = new FileWriter(FILE_PATH)) {
			GSON_INSTANCE.toJson(json, writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void wipeData() {
		UNLOCKED_INFUSIONS.clear();
		DISCOVERED_ITEMS.clear();
		maxRecencyIndex = 0;
		minRecencyIndex = 0;
		currentUnlockIndex = 0;
		unlockEverything = false;
	}

	public static void load() {

		wipeData();

		File file = new File(FILE_PATH);
		if (file.exists())
			try (Reader reader = new FileReader(file)) {

				JsonElement json = GSON_INSTANCE.fromJson(reader, JsonElement.class);
				CODEC.decode(JsonOps.INSTANCE, json);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	}

	private static ResourceLocation getItemKey(ItemStack stack) {
		return BuiltInRegistries.ITEM.getKey(stack.getItem());
	}

	public static boolean isItemDiscovered(ItemStack stack) {
		return isItemDiscovered(getItemKey(stack));
	}

	public static void discoverItem(ItemStack stack) {
		discoverItem(getItemKey(stack));
	}

	public static boolean isItemDiscovered(ResourceLocation stack) {
		return bypassesUnlocks() || DISCOVERED_ITEMS.contains(stack);
	}

	public static void discoverItem(ResourceLocation stack) {
		if (!isItemDiscovered(stack))
		{
			DISCOVERED_ITEMS.add(stack);
			queueSave();
		}
	}

	public static boolean isUnlocked(Holder<Property> propertyHolder) {
		ResourceLocation key = getKey(propertyHolder);
		return key != null && (bypassesUnlocks() || (UNLOCKED_INFUSIONS.containsKey(key) && UNLOCKED_INFUSIONS.get(key).unlocked));
	}

	public static int getUnlockIndex(Holder<Property> propertyHolder) {
		ResourceLocation key = getKey(propertyHolder);
		return UNLOCKED_INFUSIONS.containsKey(key) ?
				-UNLOCKED_INFUSIONS.get(key).unlockIndex :
				0;
	}

	public static int getRecencyIndex(Holder<Property> propertyHolder) {
		ResourceLocation key = getKey(propertyHolder);
		return UNLOCKED_INFUSIONS.containsKey(key) ?
				-UNLOCKED_INFUSIONS.get(key).recencyIndex :
				0;
	}

	@Nullable
	private static ResourceLocation getKey(Holder<Property> propertyHolder) {
		return propertyHolder.unwrapKey().map(ResourceKey::location).orElse(null);
	}

	public static List<Holder<Property>> getPropertiesToUnlock(ItemStack stack) {

		List<Holder<Property>> result = new ArrayList<>();
		var innates = InfusedPropertiesHelper.getInnateProperties(stack);

		result.addAll(innates);
		result.addAll(InfusedPropertiesHelper.getInfusedProperties(stack));
		result.addAll(InfusedPropertiesHelper.getStoredProperties(stack));

		boolean includeDormants = stack.is(AlchemancyTags.Items.CODEX_DISCOVERY_ON_PICKUP);
		boolean hasInnate = stack.has(AlchemancyItems.Components.INNATE_PROPERTIES);
		List<Holder<Property>> dormants = List.of();

		if(includeDormants || hasInnate)
			dormants = AlchemancyProperties.getDormantProperties(stack);
		if(hasInnate)
			includeDormants = includeDormants || dormants.stream().anyMatch(innates::contains);

		if(includeDormants)
			result.addAll(AlchemancyProperties.getDormantProperties(stack));
		return result;
	}

	public static void unlock(Holder<Property> propertyHolder) {
		ResourceLocation key = getKey(propertyHolder);
		if(key != null && !isUnlocked(propertyHolder))
		{
			UNLOCKED_INFUSIONS.put(key, new EntryData(true));
			InfusionCodexToast.addOrUpdate(Minecraft.getInstance().getToasts(), propertyHolder);
			queueSave();
		}
	}

	public static void read(Holder<Property> propertyHolder) {
		ResourceLocation key = getKey(propertyHolder);
		if(key == null) return;

		if (!UNLOCKED_INFUSIONS.containsKey(key))
			UNLOCKED_INFUSIONS.put(key, new EntryData(false));
		var entry = UNLOCKED_INFUSIONS.get(key);
		entry.recencyIndex = ++maxRecencyIndex;
		entry.read = true;

		if(entry.recencyIndex <= 0 || entry.recencyIndex < minRecencyIndex)
			minRecencyIndex = entry.recencyIndex;

		queueSave();
	}

	public static boolean isRead(Holder<Property> propertyHolder) {
		return bypassesUnlocks() || (UNLOCKED_INFUSIONS.containsKey(getKey(propertyHolder)) && UNLOCKED_INFUSIONS.get(getKey(propertyHolder)).read);
	}

	public static boolean bypassesUnlocks() {
		return unlockEverything || (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative());
	}

	public static void queueSave() {
		dirty = true;
	}

	@SubscribeEvent
	private static void onClientTick(ClientTickEvent.Post event) {
		if(dirty)
		{
			save();
			dirty = false;
		}
	}

	@SubscribeEvent
	private static void onLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
		load();
	}

	public static class EntryData {
		public static final Codec<EntryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.optionalFieldOf("recency_index", 0).forGetter(data -> data.recencyIndex == 0 ? 0 : data.recencyIndex - (minRecencyIndex - 1)),
				Codec.INT.optionalFieldOf("unlock_index", 0).forGetter(data -> data.unlocked ? data.unlockIndex : 0),
				Codec.BOOL.optionalFieldOf("read", true).forGetter(data -> data.read),
				Codec.BOOL.optionalFieldOf("unlocked", true).forGetter(data -> data.unlocked)
		).apply(instance, EntryData::new));

		public int unlockIndex;
		protected int recencyIndex;
		protected boolean read = false;
		protected boolean unlocked;

		public EntryData(int recencyIndex, int unlockIndex, boolean read, boolean unlocked) {
			this.unlocked = unlocked;
			this.recencyIndex = recencyIndex;
			this.read = read;
			this.unlockIndex = !unlocked ? 0 : unlockIndex <= 0 ? ++currentUnlockIndex : unlockIndex;
		}

		public EntryData(boolean unlocked) {
			this.unlocked = unlocked;
			this.unlockIndex = ++currentUnlockIndex;
		}
	}
}
