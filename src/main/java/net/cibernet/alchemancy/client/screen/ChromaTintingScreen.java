package net.cibernet.alchemancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.cibernet.alchemancy.Alchemancy;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.ChromatizeC2SPayload;
import net.cibernet.alchemancy.network.ResetItemTintC2SPayload;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;

public class ChromaTintingScreen extends Screen {

	private final ItemStack affectedItem;
	private final Integer[] originalTint;

	private HeaderAndFooterLayout layout;
	private ColorSlider hueSlider;
	private ColorSlider saturationSlider;
	private ColorSlider brightnessSlider;
	private EditBox hexInput = null;

	private static final ResourceLocation HUE_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "chromatize/slider_hue");
	private static final ResourceLocation SATURATION_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "chromatize/slider_saturation");
	private static final ResourceLocation BRIGHTNESS_SLIDER_SPRITE = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "chromatize/slider_brightness");
	private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");

	private static final int MAX_HUE = 720;
	private static final int MAX_SB = 200;

	public ChromaTintingScreen(ItemStack stack) {
		super(Component.translatable("screen.chromachine.title"));
		this.affectedItem = stack;
		originalTint = AlchemancyProperties.TINTED.value().getData(stack);
	}

	@Override
	protected void init() {
		super.init();

		layout = new HeaderAndFooterLayout(this, 32, 32);
		LinearLayout footer = layout.addToFooter(LinearLayout.horizontal()).spacing(5);
		footer.defaultCellSetting().alignHorizontallyCenter();
		footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, p_329727_ -> {
			if(originalTint == null || originalTint.length == 0)
				InfusedPropertiesHelper.removeProperty(affectedItem, AlchemancyProperties.TINTED);
			else AlchemancyProperties.TINTED.value().setData(affectedItem, originalTint);
			this.onClose();
		}).width(100).build());
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, p_329727_ -> {
			PacketDistributor.sendToServer(new ChromatizeC2SPayload(getColor()));
			this.onClose();
		}).width(100).build());

		LinearLayout header = layout.addToHeader(LinearLayout.vertical()).spacing(5);
		header.addChild(new StringWidget(width, 16, getTitle(), this.font).alignCenter());

		LinearLayout body = LinearLayout.vertical().spacing(10);
		layout.addToContents(body);

		var colors = AlchemancyProperties.TINTED.get().getData(affectedItem);
		int color = colors.length > 0 ? colors[0] : 0xFFFFFFFF;
		var hsb = Color.RGBtoHSB(FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color), new float[3]);

		hueSlider = new ColorSlider(0, 0, 200, 20, MAX_HUE, hsb[0] * MAX_HUE, ColorComponent.HUE, HUE_SLIDER_SPRITE);
		body.addChild(hueSlider);
		saturationSlider = new ColorSlider(0, 0, 200, 20, MAX_SB, hsb[1] * MAX_SB, ColorComponent.SATURATION, SATURATION_SLIDER_SPRITE);
		body.addChild(saturationSlider);
		brightnessSlider = new ColorSlider(0, 0, 200, 20, MAX_SB, hsb[2] * MAX_SB, ColorComponent.BRIGHTNESS, BRIGHTNESS_SLIDER_SPRITE);
		body.addChild(brightnessSlider);

		LinearLayout bottom = LinearLayout.horizontal().spacing(20);
		LinearLayout bottomLeft = LinearLayout.vertical().spacing(10);

		body.addChild(bottom);
		bottom.addChild(bottomLeft);

		LinearLayout hexDiv = LinearLayout.horizontal().spacing(2);
		bottomLeft.addChild(hexDiv);

		LinearLayout hexSymbol = LinearLayout.vertical();
		hexSymbol.defaultCellSetting().alignVerticallyMiddle().paddingTop(6);
		hexSymbol.addChild(new StringWidget(Component.literal("#"), font));
		hexDiv.addChild(hexSymbol);

		if (hexInput == null) {
			hexInput = new EditBox(font, 48, 20, Component.empty()) {

				@Override
				public void insertText(@NotNull String textToWrite) {
					super.insertText(textToWrite.toUpperCase().replaceAll("(?![A-F]|[0-9])[\\s\\S]", ""));
				}

				@Override
				public boolean charTyped(char codePoint, int modifiers) {
					codePoint = Character.toUpperCase(codePoint);
					if (codePoint >= 'A' && codePoint <= 'F'
							|| codePoint >= '0' && codePoint <= '9')
						return super.charTyped(codePoint, modifiers);
					else return false;
				}

				@Override
				public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

					if (keyCode == GLFW.GLFW_KEY_ENTER) {
						updateColorFromHex();
						minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
						return true;
					}
					return super.keyPressed(keyCode, scanCode, modifiers);
				}
			};
			hexInput.setMaxLength(6);
			hexInput.setValue(ColorUtils.colorToHexString(color).substring(2));
		}
		hexDiv.addChild(hexInput);
		hexDiv.addChild(Button.builder(Component.translatable("screen.chromachine.apply_hex"), p_329727_ -> updateColorFromHex()).width(64).build());

		bottomLeft.addChild(Button.builder(Component.translatable("screen.chromachine.reset"), button -> {
			hexInput.setValue("FFFFFF");
			hueSlider.setValue(0);
			saturationSlider.setValue(0);
			brightnessSlider.setValue(MAX_SB);

			PacketDistributor.sendToServer(new ResetItemTintC2SPayload());
			InfusedPropertiesHelper.removeProperty(affectedItem, AlchemancyProperties.TINTED);
		}).width(64).build());

		bottom.addChild(new ItemDisplayWidget(48, affectedItem));

		layout.visitWidgets(this::addRenderableWidget);
		layout.arrangeElements();
	}

	protected void updateColorFromHex() {
		int hexColor = FastColor.ARGB32.color(255, Integer.parseInt(hexInput.getValue(), 16));
		var hexHsb = Color.RGBtoHSB(FastColor.ARGB32.red(hexColor), FastColor.ARGB32.green(hexColor), FastColor.ARGB32.blue(hexColor), new float[3]);

		hueSlider.setValue(hexHsb[0] * MAX_HUE);
		saturationSlider.setValue(hexHsb[1] * MAX_SB);
		brightnessSlider.setValue(hexHsb[2] * MAX_SB);
		hexInput.setValue(ColorUtils.colorToHexString(hexColor).substring(2));
		CommonUtils.applyChromaTint(affectedItem, hexColor);
	}

	protected float getHue() {
		return hueSlider.getValueForColor();
	}

	protected float getSaturation() {
		return saturationSlider.getValueForColor();
	}

	protected float getBrightness() {
		return brightnessSlider.getValueForColor();
	}

	protected int getColor() {
		return Color.HSBtoRGB(getHue(), getSaturation(), getBrightness());
	}

	@Override
	public void onClose() {
		super.onClose();
		if(!Arrays.equals(originalTint, AlchemancyProperties.TINTED.get().getData(affectedItem)))
			PacketDistributor.sendToServer(new ChromatizeC2SPayload(getColor()));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

		RenderSystem.enableBlend();

		guiGraphics.blit(Screen.INWORLD_HEADER_SEPARATOR, 0, layout.getHeaderHeight() - 2, 0.0F, 0.0F, width, 2, 32, 2);
		guiGraphics.blit(Screen.INWORLD_FOOTER_SEPARATOR, 0, height - layout.getFooterHeight(), 0.0F, 0.0F, width, 2, 32, 2);

		guiGraphics.blit(
				INWORLD_MENU_LIST_BACKGROUND,
				0,
				layout.getHeaderHeight(),
				layout.getWidth(),
				layout.getHeight() - layout.getFooterHeight(),
				width,
				height - (layout.getHeaderHeight() + layout.getFooterHeight()),
				32,
				32
		);

		RenderSystem.disableBlend();
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	public class ItemDisplayWidget extends AbstractWidget {

		private final ItemStack stack;

		public ItemDisplayWidget(int size, ItemStack stack) {
			this(size, size, stack);
		}

		public ItemDisplayWidget(int width, int height, ItemStack stack) {
			super(0, 0, width, height, stack.getDisplayName());
			this.stack = stack;
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

			PoseStack poseStack = guiGraphics.pose();
			float xs = getWidth() / 16f;
			float ys = getHeight() / 16f;

			poseStack.pushPose();
			poseStack.scale(xs, ys, 1);
			guiGraphics.renderFakeItem(stack, (int) (getX() / xs), (int) (getY() / ys));
			poseStack.popPose();

			if (mouseX >= getX() && mouseX <= getX() + getWidth() &&
					mouseY >= getY() && mouseY <= getY() + getHeight())
				guiGraphics.renderTooltip(font, stack, mouseX, mouseY);
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

		}
	}

	public class ColorSlider extends ExtendedSlider {

		private static final ResourceLocation BACK_SPRITE = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "chromatize/slider_back");
		private static final ResourceLocation HIGHLIGHT_SPRITE = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "chromatize/slider_highlight");

		private static final ResourceLocation NEW_ENTRY_ICON = ResourceLocation.fromNamespaceAndPath(Alchemancy.MODID, "infusion_codex/new_entry_icon");

		private final ColorComponent component;
		private final ResourceLocation sliderSprite;

		public ColorSlider(int x, int y, int width, int height, double maxValue, double currentValue, ColorComponent component, ResourceLocation sliderSprite) {
			super(x, y, width, height, Component.empty(), Component.empty(), 0, maxValue, currentValue, false);
			this.component = component;
			this.sliderSprite = sliderSprite;
		}

		public float getValueForColor() {
			return (float) value;
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

			var color = Color.HSBtoRGB(getHue(),
					component.ordinal() > ColorComponent.SATURATION.ordinal() ? getSaturation() : 1,
					component.ordinal() > ColorComponent.BRIGHTNESS.ordinal() ? getBrightness() : 1);

			Minecraft minecraft = Minecraft.getInstance();
			guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();

			guiGraphics.setColor(FastColor.ARGB32.red(color) / 255f, FastColor.ARGB32.green(color) / 255f, FastColor.ARGB32.blue(color) / 255f, this.alpha);
			guiGraphics.blitSprite(BACK_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			guiGraphics.setColor(1, 1, 1, this.alpha);

			guiGraphics.blitSprite(sliderSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			if (this.isFocused())
				guiGraphics.blitSprite(HIGHLIGHT_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			guiGraphics.blitSprite(this.getHandleSprite(), this.getX() + (int) (this.value * (double) (this.width - 8)), this.getY(), 8, this.getHeight());

			guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			int i = this.active ? 16777215 : 10526880;
			this.renderScrollingString(guiGraphics, minecraft.font, 2, i | Mth.ceil(this.alpha * 255.0F) << 24);
		}

		@Override
		protected void applyValue() {
			int color = getColor();
			CommonUtils.applyChromaTint(affectedItem, color);
			hexInput.setValue(ColorUtils.colorToHexString(color).substring(2));
		}
	}


	public enum ColorComponent {
		HUE,
		SATURATION,
		BRIGHTNESS
	}
}
