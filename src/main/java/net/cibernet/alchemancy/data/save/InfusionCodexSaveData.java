package net.cibernet.alchemancy.data.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.io.*;
import java.util.*;

@EventBusSubscriber(Dist.CLIENT)
public class InfusionCodexSaveData {

	public static final String FILE_PATH = Alchemancy.MODID + "/infusion_codex_save_data.json";
	private static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private static boolean unlockEverything = false;
	private static int maxRecencyIndex = 0;
	private static final Map<Holder<Property>, EntryData> UNLOCKED_INFUSIONS = new HashMap<>();
	private static final List<ResourceLocation> DISCOVERED_ITEMS = new ArrayList<>();

	private static final Codec<Unit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("nerd_mode").forGetter(data -> java.util.Optional.of(unlockEverything)),
			Codec.INT.optionalFieldOf("current_recency", 0).forGetter(data -> maxRecencyIndex),
			Codec.unboundedMap(Property.CODEC, EntryData.CODEC).fieldOf("unlocked_infusions").forGetter(data -> UNLOCKED_INFUSIONS),
			Codec.list(ResourceLocation.CODEC).fieldOf("discovered_items").forGetter(data -> DISCOVERED_ITEMS)
	).apply(instance, (nerdMode, currentRecency, unlockedInfusions, discoveredItems) -> {

		unlockEverything = nerdMode.orElse(false);
		maxRecencyIndex = currentRecency;

		UNLOCKED_INFUSIONS.clear();
		UNLOCKED_INFUSIONS.putAll(unlockedInfusions);
		DISCOVERED_ITEMS.clear();
		DISCOVERED_ITEMS.addAll(discoveredItems);

		return Unit.INSTANCE;
	}));

	public static void save(HolderLookup.Provider registryAccess) {

		new File(Alchemancy.MODID).mkdirs();
		try (Writer writer = new FileWriter(FILE_PATH)) {
			JsonElement json = CODEC.encodeStart(registryAccess.createSerializationContext(JsonOps.INSTANCE), Unit.INSTANCE).getOrThrow();
			GSON_INSTANCE.toJson(json, writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void load(HolderLookup.Provider registryAccess) {

		File file = new File(FILE_PATH);
		if (file.exists())
			try (Reader reader = new FileReader(file)) {

				JsonElement json = GSON_INSTANCE.fromJson(reader, JsonElement.class);
				CODEC.decode(registryAccess.createSerializationContext(JsonOps.INSTANCE), json);
				EntryData.resetUnlockIndex();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		unlock(AlchemancyProperties.SHOCKING);
	}

	private static ResourceLocation getItemKey(ItemStack stack) {
		return BuiltInRegistries.ITEM.getKey(stack.getItem());
	}

	public static boolean isItemDiscovered(ItemStack stack) {
		return DISCOVERED_ITEMS.contains(getItemKey(stack));
	}

	public static void discoverItem(ItemStack stack) {
		if (!isItemDiscovered(stack))
			DISCOVERED_ITEMS.add(getItemKey(stack));
	}

	public static boolean isUnlocked(Holder<Property> propertyHolder) {
		return unlockEverything || UNLOCKED_INFUSIONS.containsKey(propertyHolder);
	}

	public static int getUnlockIndex(Holder<Property> propertyHolder) {
		return UNLOCKED_INFUSIONS.containsKey(propertyHolder) ?
				UNLOCKED_INFUSIONS.get(propertyHolder).unlockIndex :
				0;
	}

	public static int getRecencyIndex(Holder<Property> propertyHolder) {
		return UNLOCKED_INFUSIONS.containsKey(propertyHolder) ?
				UNLOCKED_INFUSIONS.get(propertyHolder).recencyIndex :
				0;
	}

	public static void unlock(Holder<Property> propertyHolder) {
		UNLOCKED_INFUSIONS.put(propertyHolder, new EntryData());
	}

	public static void read(Holder<Property> propertyHolder) {
		if (UNLOCKED_INFUSIONS.containsKey(propertyHolder)) {
			var entry = UNLOCKED_INFUSIONS.get(propertyHolder);
			entry.recencyIndex = maxRecencyIndex++;
			entry.read = true;
		}

	}

	public static boolean isRead(Holder<Property> propertyHolder) {
		return unlockEverything || (UNLOCKED_INFUSIONS.containsKey(propertyHolder) && UNLOCKED_INFUSIONS.get(propertyHolder).read);
	}

	@SubscribeEvent
	private static void onSave(PlayerEvent.PlayerLoggedOutEvent event) {
		save(event.getEntity().registryAccess());
		System.out.println();
	}

	@SubscribeEvent
	private static void onSave(PlayerEvent.PlayerLoggedInEvent event) {
		load(event.getEntity().registryAccess());
		System.out.println();
	}

	private static class EntryData {
		public static final Codec<EntryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.optionalFieldOf("recency_index", 0).forGetter(data -> data.recencyIndex),
				Codec.BOOL.optionalFieldOf("read", true).forGetter(data -> data.read)
		).apply(instance, EntryData::new));
		public int unlockIndex;

		protected int recencyIndex;
		protected boolean read = false;

		private static int currentUnlockIndex = 0;

		public EntryData(int recencyIndex, boolean read) {
			this();
			this.recencyIndex = recencyIndex;
			this.read = read;
		}

		public EntryData() {
			unlockIndex = currentUnlockIndex++;
		}

		private static void resetUnlockIndex() {
			currentUnlockIndex = 0;
		}
	}
}
