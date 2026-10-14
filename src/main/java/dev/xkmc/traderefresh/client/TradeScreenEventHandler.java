package dev.xkmc.traderefresh.client;

import dev.xkmc.traderefresh.compat.RecipeViewerMenu;
import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.init.TradeRefresh;
import dev.xkmc.traderefresh.network.RefreshToServer;
import dev.xkmc.traderefresh.network.TRNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.MerchantMenu;

public class TradeScreenEventHandler {

	private static int pendingRecipeViewerCheck = 0;
	private static MerchantMenu staller = null;

	public static void init() {
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof MerchantScreen merchantScreen) {
				RefreshButton.register(merchantScreen);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			onClientTick(client);
		});
	}

	public static void tryRefresh(MerchantScreen gui, boolean playSound) {
		if (staller == gui.getMenu()) return;
		if (RecipeViewerMenu.anythingMatched(gui.getMenu())) {
			return;
		}
		if (!TRConfig.SERVER.alwaysAllowRefresh && gui.getMenu().getTraderXp() > 0) {
			return;
		}
		if (playSound) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		staller = gui.getMenu();
		TRNetworking.sendToServer(new RefreshToServer());
		pendingRecipeViewerCheck = 20;
	}

	private static void onClientTick(Minecraft client) {
		if (pendingRecipeViewerCheck <= 0) return;
		pendingRecipeViewerCheck--;
		if (client.screen instanceof MerchantScreen gui) {
			if (staller == gui.getMenu()) return;
			if (RecipeViewerMenu.anythingMatched(gui.getMenu())) {
				pendingRecipeViewerCheck = 0;
				client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.0F));
			}
		}
	}

}
