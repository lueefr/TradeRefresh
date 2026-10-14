package dev.xkmc.traderefresh.common;

import dev.xkmc.traderefresh.init.TRConfig;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class RestockEventHandler {

	public static void init() {
		UseEntityCallback.EVENT.register(RestockEventHandler::onMobInteract);
	}

	private static InteractionResult onMobInteract(Player player, Level level, InteractionHand hand,
												   net.minecraft.world.entity.Entity target,
												   @Nullable EntityHitResult hitResult) {
		if (!TRConfig.SERVER.allowEmeraldBlockForceRestock) return InteractionResult.PASS;
		ItemStack itemStack = player.getItemInHand(hand);
		if (!itemStack.is(Items.EMERALD_BLOCK)) return InteractionResult.PASS;
		if (!(target instanceof Villager villager)) return InteractionResult.PASS;

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (villager.getVillagerXp() > 0 && villager.needsToRestock()) {
			villager.resetNumberOfRestocks();
			villager.restock();
			itemStack.shrink(1);
			level.broadcastEntityEvent(villager, EntityEvent.VILLAGER_HAPPY);
			villager.playSound(SoundEvents.VILLAGER_YES, 1, villager.getVoicePitch());
			return InteractionResult.CONSUME;
		} else {
			villager.setUnhappyCounter(40);
			villager.playSound(SoundEvents.VILLAGER_NO, 1, villager.getVoicePitch());
			return InteractionResult.FAIL;
		}
	}

}
