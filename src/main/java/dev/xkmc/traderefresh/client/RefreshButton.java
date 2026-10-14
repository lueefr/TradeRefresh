package dev.xkmc.traderefresh.client;

import dev.xkmc.traderefresh.init.Keys;
import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.init.TradeRefresh;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Refresh button shown on the merchant screen.
 *
 * <p>The button is registered as a regular screen widget, so it is extracted
 * together with the rest of the screen and stays inside the screen's render
 * transforms (for example SmoothGui's animated translation). The widget
 * tooltip is emitted in the deferred tooltip pass and therefore always draws
 * above the button.
 */
public class RefreshButton extends Button {

	private static final Identifier TEXTURE_NORMAL = TradeRefresh.loc("textures/gui/refresh_button.png");
	private static final Identifier TEXTURE_DISABLED = TradeRefresh.loc("textures/gui/refresh_button_disabled.png");
	private static final Identifier TEXTURE_HIGHLIGHTED = TradeRefresh.loc("textures/gui/refresh_button_highlighted.png");
	private static final Identifier TEXTURE_PRESSED = TradeRefresh.loc("textures/gui/refresh_button_pressed.png");
	private static final Identifier TEXTURE_PRESSED_HIGHLIGHTED = TradeRefresh.loc("textures/gui/refresh_button_pressed_highlighted.png");

	private static final int WIDTH = 18;
	private static final int HEIGHT = 18;

	private final MerchantScreen parent;
	private boolean pressed;

	public RefreshButton(MerchantScreen parent) {
		super(getButtonX(parent), getButtonY(parent), WIDTH, HEIGHT,
				Component.translatable("traderefresh.button.narration"),
				b -> {
				},
				DEFAULT_NARRATION);
		this.parent = parent;
	}

	public static void register(MerchantScreen screen) {
		Screens.getWidgets(screen).add(new RefreshButton(screen));

		ScreenKeyboardEvents.afterKeyPress(screen).register((scr, event) -> {
			if (Keys.REFRESH.map.matches(event)) {
				TradeScreenEventHandler.tryRefresh(screen, true);
			}
		});
	}

	private static int getButtonX(MerchantScreen parent) {
		if (TRConfig.CLIENT.buttonSide == TRConfig.ButtonSide.LEFT) {
			return parent.leftPos + 102;
		}
		return parent.leftPos + parent.imageWidth - 22;
	}

	private static int getButtonY(MerchantScreen parent) {
		if (TRConfig.CLIENT.buttonSide == TRConfig.ButtonSide.LEFT) {
			return parent.topPos + 17;
		}
		return parent.topPos + 4;
	}

	@Override
	public int getX() {
		return getButtonX(parent);
	}

	@Override
	public int getY() {
		return getButtonY(parent);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.active = TRConfig.SERVER.alwaysAllowRefresh || parent.getMenu().getTraderXp() == 0;

		if (this.active) {
			Component tooltip = Component.translatable("traderefresh.button.tooltip",
					Keys.REFRESH.map.getTranslatedKeyMessage());
			String recipeViewerName = getRecipeViewerName();
			if (recipeViewerName != null) {
				tooltip = tooltip.copy().append("\n")
						.append(Component.translatable("traderefresh.tooltip_recipe_viewer", recipeViewerName)
								.withStyle(ChatFormatting.GRAY));
			}
			this.setTooltip(Tooltip.create(tooltip));
		} else {
			this.setTooltip(Tooltip.create(Component.translatable("traderefresh.button.disabled")));
		}

		Identifier texture;
		if (!this.active) {
			texture = TEXTURE_DISABLED;
		} else if (pressed) {
			texture = this.isHovered() ? TEXTURE_PRESSED_HIGHLIGHTED : TEXTURE_PRESSED;
		} else if (this.isHovered()) {
			texture = TEXTURE_HIGHLIGHTED;
		} else {
			texture = TEXTURE_NORMAL;
		}

		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (this.active && this.isMouseOver(event.x(), event.y())) {
			pressed = true;
		}
		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (this.isHovered() && this.active) {
			TradeScreenEventHandler.tryRefresh(parent, false);
		}
		pressed = false;
		return super.mouseReleased(event);
	}

	private static String getRecipeViewerName() {
		if (FabricLoader.getInstance().isModLoaded("jei")) return "JEI";
		return null;
	}

}
