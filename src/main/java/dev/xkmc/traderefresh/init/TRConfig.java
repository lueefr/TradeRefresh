package dev.xkmc.traderefresh.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TRConfig {

	public enum ButtonSide {
		LEFT, RIGHT;

		public Component getTranslatedName() {
			return Component.translatable("traderefresh.configuration.enum.buttonSide." + name());
		}
	}

	public static class Client {
		public boolean showEnchProperties = false;
		public ButtonSide buttonSide = ButtonSide.LEFT;
	}

	public static class Server {
		public boolean alwaysAllowRefresh = false;
		public boolean allowEmeraldBlockForceRestock = true;
	}

	public static Client CLIENT = new Client();
	public static Server SERVER = new Server();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static Path clientFile;
	private static Path serverFile;

	public static void init() {
		Path configDir = FabricLoader.getInstance().getConfigDir();
		clientFile = configDir.resolve("traderefresh-client.json");
		serverFile = configDir.resolve("traderefresh-server.json");
		CLIENT = load(clientFile, Client.class);
		SERVER = load(serverFile, Server.class);
		saveClient();
		saveServer();
	}

	private static <T> T load(Path path, Class<T> clazz) {
		if (Files.exists(path)) {
			try {
				String json = Files.readString(path);
				T result = GSON.fromJson(json, clazz);
				if (result != null) return result;
			} catch (IOException | com.google.gson.JsonSyntaxException e) {
				TradeRefresh.LOGGER.error("Failed to read config {}", path, e);
			}
		}
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void save(Path path, Object config) {
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(config));
		} catch (IOException e) {
			TradeRefresh.LOGGER.error("Failed to write config {}", path, e);
		}
	}

	public static void saveClient() {
		if (clientFile != null) save(clientFile, CLIENT);
	}

	public static void saveServer() {
		if (serverFile != null) save(serverFile, SERVER);
	}

}
