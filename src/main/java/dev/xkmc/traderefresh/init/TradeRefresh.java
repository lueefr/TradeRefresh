package dev.xkmc.traderefresh.init;

import dev.xkmc.traderefresh.common.RestockEventHandler;
import dev.xkmc.traderefresh.network.TRNetworking;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TradeRefresh implements ModInitializer {

	public static final String MODID = "traderefresh";
	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		TRConfig.init();
		TRNetworking.registerPayloads();
		TRNetworking.initServer();
		RestockEventHandler.init();
	}

	public static Identifier loc(String id) {
		return Identifier.fromNamespaceAndPath(MODID, id);
	}

}
