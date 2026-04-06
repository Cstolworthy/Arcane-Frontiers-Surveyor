package arcanefrontiers.surveyor.atlas;

import arcanefrontiers.surveyor.item.AtlasItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class AtlasDiscoveryTracker {
    private static final int DISCOVERY_INTERVAL_TICKS = 20;
    private static final int FALLBACK_REVEAL_RADIUS_CHUNKS = 6;

    private AtlasDiscoveryTracker() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.tickCount % DISCOVERY_INTERVAL_TICKS != 0) {
            return;
        }

        ItemStack atlasStack = findHeldAtlas(serverPlayer);
        if (atlasStack.isEmpty()) {
            return;
        }

        ChunkPos chunkPos = serverPlayer.chunkPosition();
        AtlasData.setCenterChunk(atlasStack, chunkPos.x, chunkPos.z);
        int revealRadiusChunks = getRevealRadiusChunks(serverPlayer);

        for (int chunkX = chunkPos.x - revealRadiusChunks; chunkX <= chunkPos.x + revealRadiusChunks; chunkX++) {
            for (int chunkZ = chunkPos.z - revealRadiusChunks; chunkZ <= chunkPos.z + revealRadiusChunks; chunkZ++) {
                if (!AtlasData.ensurePageAvailable(atlasStack, serverPlayer, chunkX, chunkZ)) {
                    continue;
                }

                int chunkColor = AtlasColorSampler.sampleChunkColor(serverPlayer.level(), chunkX, chunkZ);
                AtlasData.addDiscoveredChunk(atlasStack, chunkX, chunkZ, chunkColor);
            }
        }
    }

    private static int getRevealRadiusChunks(ServerPlayer player) {
        if (player.getServer() == null) {
            return FALLBACK_REVEAL_RADIUS_CHUNKS;
        }

        int viewDistance = player.getServer().getPlayerList().getViewDistance();
        if (viewDistance <= 0) {
            return FALLBACK_REVEAL_RADIUS_CHUNKS;
        }

        return Math.max(1, viewDistance - 1);
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
