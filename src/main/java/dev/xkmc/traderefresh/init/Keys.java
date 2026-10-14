package dev.xkmc.traderefresh.init;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum Keys {
	REFRESH("key.traderefresh.refresh", GLFW.GLFW_KEY_T, KeyMapping.Category.register(TradeRefresh.loc("main")));

	public final KeyMapping map;

	Keys(String id, int key, KeyMapping.Category category) {
		map = new KeyMapping(id, key, category);
	}

}
