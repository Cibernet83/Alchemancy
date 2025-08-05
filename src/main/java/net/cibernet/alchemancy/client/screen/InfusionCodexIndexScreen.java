package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.special.InfusionCodexProperty;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Comparator;
import java.util.Map;

public class InfusionCodexIndexScreen extends Screen {

	private static final ResourceLocation NEW_ENTRY_ICON = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "infusion_codex/new_entry_icon");

	private PropertyList propertyList;
	private EditBox searchBar;
	private HeaderAndFooterLayout layout;

	private final ItemStack inspectedItem;
	private final Screen previousScreen;

	private SortOrder sortOrder = SortOrder.ALPHABETICAL;

	private int unlockedEntryCount = 0;
	private ObjectArrayList<Map.Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry>> entries;

	public InfusionCodexIndexScreen(Component title) {
		super(title);
		inspectedItem = ItemStack.EMPTY;
		this.previousScreen = Minecraft.getInstance().screen;
	}

	public InfusionCodexIndexScreen(ItemStack inspectedItem) {
		super(inspectedItem.getDisplayName());
		this.inspectedItem = inspectedItem;
		this.previousScreen = Minecraft.getInstance().screen;
	}

	@Override
	public void onClose() {
		minecraft.setScreen(previousScreen);
	}

	@Override
	protected void init() {

		generateEntries();

		layout = new HeaderAndFooterLayout(this, 48, 32);
		LinearLayout footer = layout.addToFooter(LinearLayout.vertical()).spacing(5);
		footer.defaultCellSetting().alignHorizontallyCenter();
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> this.onClose()).width(200).build());

		LinearLayout header = layout.addToHeader(LinearLayout.vertical()).spacing(2);
		if (!inspectedItem.isEmpty())
			header.addChild(new StringWidget(200, 9, Component.translatable("screen.infusion_codex.inspecting").withStyle(ChatFormatting.GRAY), this.font).alignCenter());


		MutableComponent title = Component.empty().append(this.title);
		if(inspectedItem.isEmpty() && !InfusionCodexSaveData.bypassesUnlocks())
			title = title.append(Component.translatable("screen.infusion_codex.unlocked_counter", unlockedEntryCount, entries.size())
					.withStyle(unlockedEntryCount == entries.size() ? ChatFormatting.GOLD : ChatFormatting.WHITE));

		header.addChild(new StringWidget(200, !inspectedItem.isEmpty() ? 18 : 9, title, this.font).alignCenter());


		LinearLayout searchDiv = header.addChild(LinearLayout.horizontal());
		if (searchBar == null) {
			searchBar = new EditBox(font, 200, 16, Component.translatable("narrator.infusion_codex.search_bar")) {
				@Override
				public boolean charTyped(char codePoint, int modifiers) {
					if (super.charTyped(codePoint, modifiers)) {
						updatePropertyList();
						return true;
					}
					return false;
				}

				@Override
				public void deleteChars(int num) {
					super.deleteChars(num);
					if (num != 0)
						updatePropertyList();
				}
			};
			searchBar.setHint(Component.translatable("screen.infusion_codex.search_bar").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
		}

		searchDiv.addChild(searchBar);
		searchDiv.addChild(Button.builder(sortOrder.buttonLabel, (button) -> {
			sortOrder = SortOrder.values()[(sortOrder.ordinal() + 1) % SortOrder.values().length];
			button.setMessage(sortOrder.buttonLabel);
			button.setTooltip(sortOrder.tooltip);
			updatePropertyList();
		}).tooltip(sortOrder.tooltip).size(24, 16).build());

		layout.visitWidgets(this::addRenderableWidget);

		updatePropertyList();
		layout.arrangeElements();
	}

	private void generateEntries() {

		var entrySet = inspectedItem.isEmpty() ? CodexEntryReloadListenener.getEntries() : InfusionCodexProperty.inspectItem(minecraft.player, inspectedItem);

		var objectarraylist = new ObjectArrayList<>(entrySet.entrySet());
		objectarraylist.removeIf(entry -> !InfusionCodexSaveData.isUnlocked(entry.getKey()) && entry.getKey().is(AlchemancyTags.Properties.CODEX_HIDDEN));
		unlockedEntryCount = (int) objectarraylist.stream().filter(entry -> InfusionCodexSaveData.isUnlocked(entry.getKey())).count();
		
		entries = objectarraylist;
		sortEntries();
	}

	private void sortEntries() {

		entries.sort(Comparator.comparing(entry -> entry.getKey().getKey()));
		entries.sort((o1, o2) -> sortOrder.sortFunction.compare(o1.getKey(), o2.getKey()));
		entries.sort(Comparator.comparing(entry -> !InfusionCodexSaveData.isUnlocked(entry.getKey())));
	}

	private void updatePropertyList() {

		sortEntries();

		var scroll = propertyList == null ? 0 : propertyList.getScrollAmount();

		removeWidget(propertyList);
		this.propertyList = new PropertyList(minecraft);
		propertyList.setScrollAmount(scroll);

		addRenderableWidget(propertyList);
		propertyList.updateSize(width, layout);
		layout.arrangeElements();
	}

	private Component tooltip = null;

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		if (tooltip != null) {
			guiGraphics.renderTooltip(font, tooltip, mouseX, mouseY);
			tooltip = null;
		}
	}

	public enum SortOrder {
		ALPHABETICAL("alphabetical", (o1, o2) -> 0),
		RECENCY("recency", Comparator.comparingInt(InfusionCodexSaveData::getRecencyIndex)),
		UNLOCK("unlock", Comparator.comparingInt(InfusionCodexSaveData::getUnlockIndex)),
		;
		final Component buttonLabel;
		final Tooltip tooltip;
		final Comparator<Holder<Property>> sortFunction;

		SortOrder(Component buttonLabel, Component tooltip, Comparator<Holder<Property>> sortFunction) {
			this.buttonLabel = buttonLabel;
			this.tooltip = Tooltip.create(Component.translatable("screen.infusion_codex.sort_order", tooltip));
			this.sortFunction = sortFunction;
		}

		SortOrder(String key, Comparator<Holder<Property>> sortFunction) {
			this(Component.translatable("screen.infusion_codex.sort_button." + key), Component.translatable("screen.infusion_codex.sort_order." + key), sortFunction);
		}
	}

	class PropertyList extends ObjectSelectionList<PropertyList.LockedEntry> {

		public PropertyList(Minecraft minecraft) {
			super(minecraft, InfusionCodexIndexScreen.this.width, InfusionCodexIndexScreen.this.height - 33 - 58, 33, 16);

			entries.stream().filter(propertyHolder -> propertyHolder.getKey().value().getName().getString().toLowerCase().contains(searchBar.getValue().toLowerCase()))
					.forEach(propertyHolder -> this.addEntry(InfusionCodexSaveData.isUnlocked(propertyHolder.getKey()) ?
							new Entry(propertyHolder.getKey(), propertyHolder.getValue()) :
							new LockedEntry()));
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			setSelected(getHovered());
			super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
		}

		@Override
		public int getRowWidth() {
			return 280;
		}

		@OnlyIn(Dist.CLIENT)
		class LockedEntry extends ObjectSelectionList.Entry<LockedEntry> {

			private static final Component NARRATOR_COMPONENT = Component.translatable("narrator.infusion_codex.locked_entry");
			private static final Component TEXT_COMPONENT = Component.translatable("screen.infusion_codex.locked_entry");
			private static final Component TOOLTIP_TEXT_COMPONENT = Component.translatable("screen.infusion_codex.locked_entry.tooltip");

			@Override
			public Component getNarration() {
				return NARRATOR_COMPONENT;
			}

			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
				int i = top + height / 2 - 9 / 2;
				int j = index % 2 == 0 ? -1 : -4539718;
				guiGraphics.drawString(InfusionCodexIndexScreen.this.font, TEXT_COMPONENT, left + 18, i, j);

				if (hovering)
					tooltip = TOOLTIP_TEXT_COMPONENT;
			}
		}

		@OnlyIn(Dist.CLIENT)
		class Entry extends LockedEntry {
			private final Holder<Property> property;
			private final CodexEntryReloadListenener.CodexEntry entry;
			private final boolean read;
			private final Component textNarration;
			private final ItemStack propertyCapsule;

			Entry(Holder<Property> property, CodexEntryReloadListenener.CodexEntry entry) {
				this.property = property;
				this.textNarration = property.value().getName();
				this.entry = entry;
				this.propertyCapsule = InfusedPropertiesHelper.createPropertyCapsule(this.property);
				this.read = InfusionCodexSaveData.isRead(property);
			}

			@Override
			public void render(
					GuiGraphics guiGraphics,
					int index,
					int top,
					int left,
					int width,
					int height,
					int mouseX,
					int mouseY,
					boolean hovering,
					float partialTick
			) {
				int i = top + height / 2 - 9 / 2;
				int j = index % 2 == 0 ? -1 : -4539718;
				PoseStack poseStack = guiGraphics.pose();

				Component name = property.value().getName(propertyCapsule);
				if (equals(getSelected()))
					name = Component.literal(name.getString()).withColor(0xFFFFFF);

				guiGraphics.renderFakeItem(propertyCapsule, left - 2, i - 4);


				if (!read) {
					poseStack.pushPose();
					poseStack.translate(0, 0, 200);
					guiGraphics.blitSprite(NEW_ENTRY_ICON, 16, 16, 0, 0, left - 2, i - 4, 16, 16);
					poseStack.popPose();
				}
//					guiGraphics.renderFakeItem(Items.BLAZE_ROD.getDefaultInstance(), left - 2, i - 4);
				guiGraphics.drawString(InfusionCodexIndexScreen.this.font, name, left + 18, i, j);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (equals(getSelected())) {
					minecraft.setScreen(new InfusionCodexEntryScreen(property, entry, InfusionCodexIndexScreen.this));
					playDownSound(Minecraft.getInstance().getSoundManager());
				}
				return true;
			}

			@Override
			public Component getNarration() {
				return textNarration;
			}
		}
	}

}
