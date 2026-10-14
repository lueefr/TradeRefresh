package dev.xkmc.traderefresh.common;

import dev.xkmc.traderefresh.init.TRConfig;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class TooltipEventHandler {

	public static void initClient() {
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if (FabricLoader.getInstance().isModLoaded("apotheosis")) return;
			if (!TRConfig.CLIENT.showEnchProperties) return;
			if (!stack.is(Items.ENCHANTED_BOOK)) return;
			var map = EnchantmentHelper.getEnchantmentsForCrafting(stack);
			if (map.size() != 1) return;
			var opt = map.entrySet().stream().findFirst();
			if (opt.isEmpty()) return;
			var e = opt.get().getKey();
			boolean enchantable = e.is(EnchantmentTags.IN_ENCHANTING_TABLE);
			boolean tradable = e.is(EnchantmentTags.TRADEABLE);
			var id = e.unwrapKey().orElseThrow();
			lines.add(getComp("enchantable", enchantable));
			lines.add(getComp("tradable", tradable));
			lines.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
		});
	}

	private static MutableComponent getComp(String str, boolean enabled) {
		return Component.translatable("traderefresh." + str + "." + enabled)
				.withStyle(enabled ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED);
	}

}
