package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.data.save.InfusionCodexSaveData;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.cibernet.alchemancy.util.PropertyFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class InfusionCodexEntryScreen extends Screen {

	private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
	private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");
	private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller_background");

	private final Holder<Property> property;
	private final CodexEntryReloadListenener.CodexEntry entry;
	private final ItemStack[] dormantItems;
	private final int undiscoveredItems;

	protected final Screen lastScreen;

	private HeaderAndFooterLayout layout;

	protected InfusionCodexEntryScreen(Holder<Property> property, CodexEntryReloadListenener.CodexEntry entry, Screen lastScreen) {
		super(property.value().getName());
		this.property = property;
		this.entry = entry;
		this.lastScreen = lastScreen;

		AtomicInteger undiscovered = new AtomicInteger();

		Ingredient ingredient = Ingredient.of(property.value().getDormantPropertyTag());
		dormantItems = ingredient.isEmpty() ? new ItemStack[0] : Arrays.stream(ingredient.getItems())
				.filter(stack -> !stack.is(Items.BARRIER))
				.filter(stack -> {
					if (InfusionCodexSaveData.isItemDiscovered(stack))
						return true;
					undiscovered.getAndIncrement();
					return false;
				})
				.toArray(ItemStack[]::new);
		undiscoveredItems = undiscovered.get();

		InfusionCodexSaveData.read(property);
	}

	@Override
	protected void init() {
		super.init();

		layout = new HeaderAndFooterLayout(this, 40, 32);
		LinearLayout footer = layout.addToFooter(LinearLayout.vertical()).spacing(5);
		footer.defaultCellSetting().alignHorizontallyCenter();
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> this.onClose()).width(200).build());

		LinearLayout header = layout.addToHeader(LinearLayout.vertical()).spacing(5);
		header.addChild(new TitleWidget(width, 16, this.font, property).alignCenter());

		header.addChild(new StringWidget(width, 9, entry.flavor().copy().withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY), this.font).alignCenter());

		layout.addToContents(new EntryBox(0, 0, width, height - layout.getHeaderHeight() - layout.getFooterHeight(), 32, 8));

		layout.visitWidgets(this::addRenderableWidget);
		layout.arrangeElements();
	}

	private MutableComponent translated(String key) {
		return Component.translatable("infusion_codex.%s.%s".formatted(property.getRegisteredName(), key));
	}

	private boolean hasTranslation(String key) {
		return I18n.exists("infusion_codex.%s.%s".formatted(property.getRegisteredName(), key));
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}

	class EntryBox extends AbstractWidget {

		final int xPadding;
		final int yPadding;

		public EntryBox(int x, int y, int width, int height, int xPadding, int yPadding) {
			super(x, y, width, height, Component.empty());
			this.xPadding = xPadding;
			this.yPadding = yPadding;
		}

		private static final int itemPadding = 2;
		private static final int itemSize = itemPadding * 2 + 16;
		private static final int SCROLLBAR_WIDTH = 6;

		private float textYPointer, entryHeight, scrollAmount;
		private boolean scrolling;

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

			float oldAmount = scrollAmount;
			scrollAmount = Mth.clamp(scrollAmount - (float) (scrollY * font.lineHeight), 0, getMaxScroll());

			return oldAmount != scrollAmount;
		}

		public float getMaxScroll() {
			return Math.max(0, this.entryHeight - getHeight());
		}

		public boolean scrollbarVisible() {
			return getMaxScroll() > 0;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {

			scrolling = (button == 0 && mouseX >= (double) this.getScrollbarPosition() && mouseX < (double) (this.getScrollbarPosition() + SCROLLBAR_WIDTH));
			return scrolling;//super.mouseClicked(mouseX, mouseY, button);
		}


		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
			if (button == 0 && this.scrolling) {
				if (mouseY < (double) this.getY()) {
					scrollAmount = 0.0f;
				} else if (mouseY > (double) this.getBottom()) {
					scrollAmount = this.getMaxScroll();
				} else {
					float d0 = Math.max(1, this.getMaxScroll());
					int i = this.height;
					int j = Mth.clamp((int) ((float) (i * i) / (this.entryHeight - getHeight())), 32, i - 8);
					float d1 = Math.max(1, d0 / (float) (i - j));
					scrollAmount = (float) Math.clamp(scrollAmount + dragY * d1, 0, getMaxScroll());
				}

				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

			renderListBackground(guiGraphics);
			guiGraphics.enableScissor(getX(), getY(), getRight(), getBottom());

			textYPointer = yPadding - scrollAmount;

			for (PropertyFunction function : entry.functions()) {
				renderFunctionParagraph(guiGraphics, function.localizationKey);
			}

			TooltipRendering tooltip = null;
			if (dormantItems.length > 0 || undiscoveredItems > 0) {
				renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex.dormant_properties"), 1.25f, 5592575);

				int itemsPerRow = (width - xPadding * 2) / itemSize;
				int itemCount = dormantItems.length;

				for (int i = 0; i < itemCount; i++) {
					ItemStack stack = dormantItems[i];
					int xx = getX() + xPadding + ((i % itemsPerRow) * itemSize);
					int yy = getY() + (int) textYPointer + ((i / itemsPerRow) * itemSize);
					guiGraphics.renderFakeItem(stack, xx, yy);

					if (mouseX >= xx - itemPadding && mouseX < xx - itemPadding + itemSize && mouseY >= yy - itemPadding && mouseY < yy - itemPadding + itemSize)
						tooltip = new ItemTooltip(stack);
				}

				if (undiscoveredItems > 0) {
					int xx = getX() + xPadding + ((itemCount % itemsPerRow) * itemSize);
					int yy = getY() + (int) textYPointer + ((itemCount / itemsPerRow) * itemSize);
					guiGraphics.drawString(font, Component.translatable("screen.infusion_codex.undiscovered_items", undiscoveredItems), xx, yy + (itemSize / 2) - (font.lineHeight / 2), 0xFFFFFF);
					itemCount++;

					if (mouseX >= xx - itemPadding && mouseX < xx - itemPadding + itemSize && mouseY >= yy - itemPadding && mouseY < yy - itemPadding + itemSize)
						tooltip = new TextTooltip(List.of(Component.translatable("screen.infusion_codex.undiscovered_items.tooltip", undiscoveredItems)));
				}

				textYPointer += (((itemCount - 1) / itemsPerRow) * itemSize) + 10;
			}

			guiGraphics.disableScissor();

			renderListSeparators(guiGraphics);

			if (this.scrollbarVisible()) {
				int l = getScrollbarPosition();
				int i1 = (int) ((float) (this.height * this.height) / this.getMaxScroll());
				i1 = Mth.clamp(i1, 32, this.height - 8);
				float k = (int) this.scrollAmount * (this.height - i1) / this.getMaxScroll() + this.getY();
				if (k < this.getY()) {
					k = this.getY();
				}

				RenderSystem.enableBlend();
				guiGraphics.blitSprite(SCROLLER_BACKGROUND_SPRITE, l, this.getY(), 6, this.getHeight());
				guiGraphics.blitSprite(SCROLLER_SPRITE, l, (int) k, 6, i1);
				RenderSystem.disableBlend();
			}

			if (tooltip != null && mouseX >= getX() && mouseX <= getRight() && mouseY >= getY() && mouseY <= getBottom())
				tooltip.apply(guiGraphics, font, mouseX, mouseY);

			entryHeight = textYPointer + 10 + scrollAmount;
		}

		private int getScrollbarPosition() {
			return getWidth() - xPadding;
		}

		private void renderFunctionParagraph(GuiGraphics guiGraphics, String functionKey) {

			//if(hasTranslation(functionKey))
			{
				renderTextLine(guiGraphics, Component.translatable("screen.infusion_codex." + functionKey), 1.25f, 5592575);
				renderTextLine(guiGraphics, translated(functionKey), 1, 0xFFFFFF);
				textYPointer += 10;
			}
		}

		private static final String FORMAT_REGEX = "\\{([^\\}]*)\\}";

		public Component processFormatting(String formatType, String value) {

			return switch (formatType.toLowerCase()) {
				case "property" -> {
					Optional<Property> property = CommonUtils.registryAccessStatic().registryOrThrow(AlchemancyProperties.REGISTRY_KEY).getOptional(ResourceLocation.parse(value));
					yield property.map(Property::getName).orElse(Component.literal(value).withColor(0xFF0000));
				}
				case "enchantment" -> {
					Optional<Enchantment> property = CommonUtils.registryAccessStatic().registryOrThrow(Registries.ENCHANTMENT).getOptional(ResourceLocation.parse(value));
					yield property.map(block -> block.description().copy().withStyle(ChatFormatting.LIGHT_PURPLE)).orElse(Component.literal(value).withColor(0xFF0000));
				}

				case "function" ->
						Component.translatable("screen.infusion_codex." + value).withStyle(ChatFormatting.BLUE);

				case "shock" ->
						Component.literal(value).withColor(AlchemancyProperties.SHOCKING.get().getColor(ItemStack.EMPTY));
				case "arcane" ->
						Component.literal(value).withColor(AlchemancyProperties.ARCANE.get().getColor(ItemStack.EMPTY));
				case "item" -> Component.literal(value).withStyle(ChatFormatting.GREEN);
				case "attribute" -> Component.literal(value).withStyle(ChatFormatting.DARK_AQUA);
				case "system" -> Component.literal(value).withStyle(ChatFormatting.AQUA);
				case "nether" -> Component.literal(value).withStyle(ChatFormatting.RED);
				case "end" -> Component.literal(value).withStyle(ChatFormatting.DARK_PURPLE);
				case "activate" -> Component.literal(value).withColor(0xFF6366);
				case "hint" -> Component.literal(value).withColor(0x00FFFF);
				default -> Component.literal(value);
			};
		}

		public void renderTextLine(GuiGraphics guiGraphics, Component text, float scale, int color) {
			PoseStack poseStack = guiGraphics.pose();

			poseStack.pushPose();
			if (scale != 1)
				poseStack.scale(scale, scale, 1);

			ArrayList<Component> things = new ArrayList<>();

			//TODO pool these on init

			String str = text.getString();
			str = str.replace("%s", "");
			str = Pattern.compile(FORMAT_REGEX).matcher(str).replaceAll(matchResult ->
			{
				String found = matchResult.group();
				found = found.substring(1, found.length() - 1);

				String[] params = found.split(" ", 2);
				things.add(processFormatting(params.length < 2 ? "" : params[0], params[params.length - 1]));
				return "%s";
			});

			MutableComponent newText = Component.empty();

			int i = 0;
			for (String s : str.split("%s")) {

				newText = newText.append(s);
				if (i < things.size())
					newText = newText.append(things.get(i));

				i++;
			}

			for (FormattedCharSequence t : font.split(newText, width - xPadding * 2)) {
				guiGraphics.drawString(font, t, (getX() + xPadding) / scale, ((getY() + textYPointer) / scale), color, true);
				textYPointer += font.lineHeight * scale;
			}

			textYPointer += font.lineHeight * 0.5f;

			poseStack.popPose();
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

		}

		protected void renderListBackground(GuiGraphics guiGraphics) {
			RenderSystem.enableBlend();
			ResourceLocation resourcelocation = INWORLD_MENU_LIST_BACKGROUND;
			guiGraphics.blit(
					resourcelocation,
					this.getX(),
					this.getY(),
					(float) this.getRight(),
					(float) (this.getBottom()),// + (int)this.getScrollAmount()),
					this.getWidth(),
					this.getHeight(),
					32,
					32
			);
			RenderSystem.disableBlend();
		}


		protected void renderListSeparators(GuiGraphics guiGraphics) {
			RenderSystem.enableBlend();
			guiGraphics.blit(Screen.INWORLD_HEADER_SEPARATOR, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
			guiGraphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
			RenderSystem.disableBlend();
		}

		interface TooltipRendering {
			void apply(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY);
		}

		record TextTooltip(List<Component> lines) implements TooltipRendering {
			@Override
			public void apply(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
				guiGraphics.renderTooltip(font, lines().stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
			}
		}

		record ItemTooltip(ItemStack stack) implements TooltipRendering {
			@Override
			public void apply(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY) {
				guiGraphics.renderTooltip(font, stack(), mouseX, mouseY);
			}
		}
	}

	private static class TitleWidget extends StringWidget {

		private final ItemStack stack;
		private final Holder<Property> propertyHolder;
		private final float alignX = 0.5f;

		public TitleWidget(int width, int height, Font font, Holder<Property> propertyHolder) {
			super(width, height, Component.empty(), font);
			this.stack = InfusedPropertiesHelper.createPropertyCapsule(propertyHolder);
			this.propertyHolder = propertyHolder;
		}

		@Override
		public Component getMessage() {
			return propertyHolder.value().getName(stack);
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

			PoseStack poseStack = guiGraphics.pose();
			poseStack.pushPose();

			int scale = 2;
			int yOff = 0;

			poseStack.scale(scale, scale, 1);

			setX(getX() / scale);
			setY(getY() / scale + yOff);
			setWidth(getWidth() / scale);
			setHeight(getHeight() / scale);

			super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
			setX(getX() * scale);
			setY((getY() - yOff) * scale);
			setWidth(getWidth() * scale);
			setHeight(getHeight() * scale);


			poseStack.popPose();

			Component component = this.getMessage();
			Font font = this.getFont();
			int i = this.getWidth();
			int j = font.width(component) * scale;
			int k = this.getX() + Math.round(this.alignX * (float) (i - j));
			int l = this.getY() + (this.getHeight() - 9) / 2;

			guiGraphics.renderFakeItem(stack, k - 20, l - 4 + (yOff * scale));
			guiGraphics.renderFakeItem(stack, k + j + 4, l - 4 + (yOff * scale));
		}
	}
}
