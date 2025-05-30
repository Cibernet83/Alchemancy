package net.cibernet.alchemancy.client.screen;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IHolderExtension;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class InfusionCodexIndexScreen extends Screen {


	private PropertyList propertyList;
	private EditBox searchBar;
	private HeaderAndFooterLayout layout;

	public InfusionCodexIndexScreen(Component title) {
		super(title);

	}

	@Override
	protected void init() {

		layout = new HeaderAndFooterLayout(this, 48, 32);
		LinearLayout footer = layout.addToFooter(LinearLayout.vertical()).spacing(5);
		footer.defaultCellSetting().alignHorizontallyCenter();
		LinearLayout linearlayout1 = footer.addChild(LinearLayout.horizontal()).spacing(5);
		//linearlayout1.addChild(Button.builder(GENERAL_BUTTON, p_96963_ -> this.setActiveList(this.statsList)).width(120).build());
		//Button button = linearlayout1.addChild(Button.builder(ITEMS_BUTTON, p_96959_ -> this.setActiveList(this.itemStatsList)).width(120).build());
		//Button button1 = linearlayout1.addChild(Button.builder(MOBS_BUTTON, p_96949_ -> this.setActiveList(this.mobsStatsList)).width(120).build());
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> this.onClose()).width(200).build());

		LinearLayout header = layout.addToHeader(LinearLayout.vertical()).spacing(5);
		header.addChild(new StringWidget(200, 18, title, this.font).alignCenter());


		if(searchBar == null) {
			searchBar = new EditBox(font, 200, 16, Component.translatable("narrator.infusion_codex.search_bar")){
				@Override
				public boolean charTyped(char codePoint, int modifiers) {
					if(super.charTyped(codePoint, modifiers))
					{
						updatePropertyList();
						return true;
					}
					return false;
				}

				@Override
				public void deleteChars(int num) {
					super.deleteChars(num);
					if(num != 0)
						updatePropertyList();
				}
			};
			searchBar.setHint(Component.translatable("screen.infusion_codex.search_bar").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
		}
		header.addChild(searchBar);

		layout.visitWidgets(this::addRenderableWidget);

		updatePropertyList();
		layout.arrangeElements();
	}

	private void updatePropertyList() {
		var scroll = propertyList == null ? 0 : propertyList.getScrollAmount();

		removeWidget(propertyList);
		this.propertyList = new PropertyList(minecraft);
		propertyList.setScrollAmount(scroll);

		addRenderableWidget(propertyList);
		propertyList.updateSize(width, layout);
		layout.arrangeElements();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	@OnlyIn(Dist.CLIENT)
	class PropertyList extends ObjectSelectionList<PropertyList.Entry> {

		public PropertyList(Minecraft minecraft) {
			super(minecraft, InfusionCodexIndexScreen.this.width, InfusionCodexIndexScreen.this.height - 33 - 58, 33, 16);
			var objectarraylist = new ObjectArrayList<>(CodexEntryReloadListenener.getEntries().entrySet().stream()
					.filter(propertyHolder -> propertyHolder.getKey().value().getName().getString().toLowerCase().contains(searchBar.getValue().toLowerCase()))
					.toList());
			objectarraylist.sort(Comparator.comparing(entry -> entry.getKey().getKey()));

			for (Map.Entry<Holder<Property>, CodexEntryReloadListenener.CodexEntry> propertyHolder : objectarraylist) {
				this.addEntry(new PropertyList.Entry(propertyHolder.getKey(), propertyHolder.getValue()));
			}
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
		class Entry extends ObjectSelectionList.Entry<PropertyList.Entry> {
			private final Holder<Property> property;
			private final CodexEntryReloadListenener.CodexEntry entry;
			private final Component textNarration;
			private final ItemStack propertyCapsule;

			Entry(Holder<Property> property, CodexEntryReloadListenener.CodexEntry entry) {
				this.property = property;
				this.textNarration = property.value().getName();
				this.entry = entry;
				this.propertyCapsule = InfusedPropertiesHelper.createPropertyCapsule(this.property);
			}

			private String getValueText() {
				return textNarration.getString();
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

//				if(hovering)
//					setSelected(this);

				int i = top + height / 2 - 9 / 2;
				int j = index % 2 == 0 ? -1 : -4539718;

				Component name = property.value().getName(propertyCapsule);
				if(equals(getSelected()))
					name = name.copy().withColor(0xFFFFFF);

				guiGraphics.renderFakeItem(propertyCapsule, left - 2, i - 4);
				guiGraphics.drawString(InfusionCodexIndexScreen.this.font, name, left + 18, i, j);
				//String s = this.getValueText();
				//guiGraphics.drawString(InfusionCodexScreen.this.font, s, left + width - InfusionCodexScreen.this.font.width(s) - 4, i, j);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if(equals(getSelected()))
					minecraft.setScreen(new InfusionCodexEntryScreen(property, entry, InfusionCodexIndexScreen.this));
				return true;
			}

			@Override
			public Component getNarration() {
				return textNarration;
			}
		}
	}

}
