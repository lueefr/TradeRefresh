package dev.xkmc.traderefresh.init;

import dev.xkmc.traderefresh.client.TradeScreenEventHandler;
import dev.xkmc.traderefresh.common.TooltipEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class TradeRefreshClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		for (Keys key : Keys.values()) {
			KeyMappingHelper.registerKeyMapping(key.map);
		}
		TradeScreenEventHandler.init();
		TooltipEventHandler.initClient();
	}

}
