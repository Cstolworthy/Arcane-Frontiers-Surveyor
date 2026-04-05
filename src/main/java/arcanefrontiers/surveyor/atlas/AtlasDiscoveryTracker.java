package arcanefrontiers.surveyor.atlas;

import arcanefrontiers.surveyor.item.AtlasItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class AtlasDiscoveryTracker {
    private AtlasDiscoveryTracker() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.tickCount % 20 != 0) {
            return;
        }

        ItemStack atlasStack = findHeldAtlas(serverPlayer);
        if (atlasStack.isEmpty()) {
            return;
        }

        ChunkPos chunkPos = serverPlayer.chunkPosition();
        AtlasData.addDiscoveredChunk(atlasStack, chunkPos.x, chunkPos.z);
    }

    private static ItemStack findHeldAtlas(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof AtlasItem) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof AtlasItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
