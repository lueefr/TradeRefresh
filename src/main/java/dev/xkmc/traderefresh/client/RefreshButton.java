package dev.xkmc.traderefresh.client;

import dev.xkmc.traderefresh.init.Keys;
import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.init.TradeRefresh;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class RefreshButton {

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
		this.parent = parent;
	}

	public static void register(MerchantScreen screen) {
		RefreshButton button = new RefreshButton(screen);

		// Render with the container foreground: this keeps the button inside screen transforms
		// (for example SmoothGui's animated translation) and leaves tooltips above it.
		ScreenEvents.afterForeground(screen).register((scr, guiGraphics, mouseX, mouseY, partialTick) -> {
			button.render(guiGraphics, mouseX, mouseY, partialTick);
		});

		ScreenMouseEvents.afterMouseClick(screen).register((scr, event, consumed) -> {
			button.onMouseClick(event.x(), event.y(), event.button());
			return consumed || button.pressed;
		});

		ScreenMouseEvents.afterMouseRelease(screen).register((scr, event, consumed) -> {
			boolean wasPressed = button.pressed;
			button.onMouseRelease(event.x(), event.y(), event.button());
			return consumed || wasPressed;
		});

		ScreenKeyboardEvents.afterKeyPress(screen).register((scr, event) -> {
			if (Keys.REFRESH.map.matches(event)) {
				TradeScreenEventHandler.tryRefresh(screen, true);
			}
		});
	}

	private int getX() {
		if (TRConfig.CLIENT.buttonSide == TRConfig.ButtonSide.LEFT) {
			return parent.leftPos + 102;
		}
		return parent.leftPos + parent.imageWidth - 22;
	}

	private int getY() {
		if (TRConfig.CLIENT.buttonSide == TRConfig.ButtonSide.LEFT) {
			return parent.topPos + 17;
		}
		return parent.topPos + 4;
	}

	private boolean isActive() {
		return TRConfig.SERVER.alwaysAllowRefresh || parent.getMenu().getTraderXp() == 0;
	}

	private boolean isMouseOver(double mouseX, double mouseY) {
		int x = getX();
		int y = getY();
		return mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT;
	}

	private void onMouseClick(double mouseX, double mouseY, int button) {
		if (button == 0 && isActive() && isMouseOver(mouseX, mouseY)) {
			pressed = true;
		}
	}

	private void onMouseRelease(double mouseX, double mouseY, int button) {
		if (button == 0 && pressed) {
			if (isMouseOver(mouseX, mouseY) && isActive()) {
				TradeScreenEventHandler.tryRefresh(parent, false);
			}
			pressed = false;
		}
	}

	public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		boolean active = isActive();
		boolean hovered = isMouseOver(mouseX, mouseY);

		Identifier texture;
		if (!active) {
			texture = TEXTURE_DISABLED;
		} else if (pressed) {
			texture = hovered ? TEXTURE_PRESSED_HIGHLIGHTED : TEXTURE_PRESSED;
		} else if (hovered) {
			texture = TEXTURE_HIGHLIGHTED;
		} else {
			texture = TEXTURE_NORMAL;
		}

		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

		if (hovered) {
			Component tooltip;
			if (active) {
				tooltip = Component.translatable("traderefresh.button.tooltip",
						Keys.REFRESH.map.getTranslatedKeyMessage());
				String recipeViewerName = getRecipeViewerName();
				if (recipeViewerName != null) {
					tooltip = tooltip.copy().append("\n")
							.append(Component.translatable("traderefresh.tooltip_recipe_viewer", recipeViewerName)
									.withStyle(ChatFormatting.GRAY));
				}
			} else {
				tooltip = Component.translatable("traderefresh.button.disabled");
			}
			guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, mouseX, mouseY);
		}
	}

	private static String getRecipeViewerName() {
		if (FabricLoader.getInstance().isModLoaded("jei")) return "JEI";
		return null;
	}

}
