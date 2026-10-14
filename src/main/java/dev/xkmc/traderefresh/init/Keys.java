package dev.xkmc.traderefresh.init;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum Keys {
	REFRESH("key.traderefresh.refresh", GLFW.GLFW_KEY_T, "key.category.traderefresh.main");

	public final KeyMapping map;

	Keys(String id, int key, String category) {
		map = new KeyMapping(id, key, category);
	}

}
