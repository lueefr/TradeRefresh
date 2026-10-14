package dev.xkmc.traderefresh.network;

import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.init.TradeRefresh;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantMenu;

public record RefreshToServer() implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<RefreshToServer> TYPE =
			new CustomPacketPayload.Type<>(TradeRefresh.loc("refresh_to_server"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RefreshToServer> CODEC =
			StreamCodec.unit(new RefreshToServer());

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RefreshToServer packet, ServerPlayer player) {
		if (!(player.containerMenu instanceof MerchantMenu gui)) return;
		if (!(gui.trader instanceof Villager villager)) return;
		if (TRConfig.SERVER.alwaysAllowRefresh) {
			if (!(player.level() instanceof ServerLevel sl)) return;
			player.doCloseContainer();
			var offer = villager.getOffers();
			offer.removeLast();
			if (offer.size() % 2 != 0) offer.removeLast();
			villager.setOffers(offer);
			villager.updateTrades(sl);
			villager.startTrading(player);
		} else {
			if (villager.getVillagerXp() > 0) return;
			player.doCloseContainer();
			villager.setOffers(null);
			villager.startTrading(player);
		}
	}

}
