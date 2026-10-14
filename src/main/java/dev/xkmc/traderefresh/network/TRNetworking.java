package dev.xkmc.traderefresh.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class TRNetworking {

	public static void registerPayloads() {
		PayloadTypeRegistry.playC2S().register(RefreshToServer.TYPE, RefreshToServer.CODEC);
	}

	public static void initServer() {
		ServerPlayNetworking.registerGlobalReceiver(RefreshToServer.TYPE, (payload, context) -> {
			context.server().execute(() -> {
				RefreshToServer.handle(payload, context.player());
			});
		});
	}

	public static void initClient() {
	}

	public static void sendToServer(RefreshToServer packet) {
		ClientPlayNetworking.send(packet);
	}

}
