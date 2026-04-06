package arcanefrontiers.surveyor.client.gui;

import arcanefrontiers.surveyor.atlas.AtlasData;
import arcanefrontiers.surveyor.atlas.AtlasMapLayout;
import arcanefrontiers.surveyor.client.SurveyorClientAccess;
import arcanefrontiers.surveyor.item.AtlasItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class AtlasOverlayRenderer {
    private static final Component TITLE = Component.translatable("screen.surveyor.atlas.title");
    private static final Component EMPTY_DESCRIPTION = Component.translatable("screen.surveyor.atlas.empty");
    private static final Component CHUNKS_LABEL = Component.translatable("screen.surveyor.atlas.discovered_chunks");
    private static final Component EMPTY_MAPS_LABEL = Component.translatable("screen.surveyor.atlas.empty_maps");
    private static final Component CONSUMED_MAPS_LABEL = Component.translatable("screen.surveyor.atlas.consumed_maps");
    private static final int MAP_RADIUS_CHUNKS = AtlasMapLayout.PAGE_RADIUS_CHUNKS;
    private static final int MAP_BACKGROUND = 0xC0101010;
    private static final int MAP_BORDER = 0xFFB08C5A;
    private static final int DISCOVERED_FALLBACK_COLOR = 0xFF3D8B5A;
    private static final int PLAYER_COLOR = 0xFFFFE46A;
    private static final int SCREEN_OVERLAY = 0x50000000;

    private AtlasOverlayRenderer() {
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!SurveyorClientAccess.isAtlasOverlayOpen()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        ItemStack atlasStack = getHeldAtlas(minecraft);
        if (atlasStack.isEmpty()) {
            SurveyorClientAccess.closeAtlasOverlay();
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        guiGraphics.fill(0, 0, width, height, SCREEN_OVERLAY);

        int centerX = width / 2;
        int maxMapSize = Math.min(width - 80, height - 140);
        int mapTileCount = MAP_RADIUS_CHUNKS * 2 + 1;
        int tileSize = Math.max(2, maxMapSize / mapTileCount);
        int mapSize = mapTileCount * tileSize;

        int mapLeft = centerX - (mapSize / 2);
        int mapTop = height / 2 - (mapSize / 2) + 18;
        int mapRight = mapLeft + mapSize;
        int mapBottom = mapTop + mapSize;

        guiGraphics.fill(mapLeft - 1, mapTop - 1, mapRight + 1, mapBottom + 1, MAP_BORDER);
        guiGraphics.fill(mapLeft, mapTop, mapRight, mapBottom, MAP_BACKGROUND);

        int playerChunkX = minecraft.player.chunkPosition().x;
        int playerChunkZ = minecraft.player.chunkPosition().z;
        int centerChunkX = AtlasData.getCenterChunkX(atlasStack, playerChunkX);
        int centerChunkZ = AtlasData.getCenterChunkZ(atlasStack, playerChunkZ);

        long[] discoveredChunks = AtlasData.getDiscoveredChunks(atlasStack);
        int[] discoveredColors = AtlasData.getDiscoveredChunkColors(atlasStack);
        for (int index = 0; index < discoveredChunks.length; index++) {
            long packedChunk = discoveredChunks[index];
            int chunkX = ChunkPos.getX(packedChunk);
            int chunkZ = ChunkPos.getZ(packedChunk);
            int relativeX = chunkX - centerChunkX;
            int relativeZ = chunkZ - centerChunkZ;

            if (Math.abs(relativeX) > MAP_RADIUS_CHUNKS || Math.abs(relativeZ) > MAP_RADIUS_CHUNKS) {
                continue;
            }

            int renderX = mapLeft + ((relativeX + MAP_RADIUS_CHUNKS) * tileSize);
            int renderY = mapTop + ((relativeZ + MAP_RADIUS_CHUNKS) * tileSize);
            int color = index < discoveredColors.length ? discoveredColors[index] : DISCOVERED_FALLBACK_COLOR;
            guiGraphics.fill(renderX, renderY, renderX + tileSize, renderY + tileSize, color);
        }

        int playerTileX = mapLeft + (MAP_RADIUS_CHUNKS * tileSize);
        int playerTileY = mapTop + (MAP_RADIUS_CHUNKS * tileSize);
        guiGraphics.fill(playerTileX, playerTileY, playerTileX + tileSize, playerTileY + tileSize, PLAYER_COLOR);

        guiGraphics.drawCenteredString(minecraft.font, TITLE, centerX, mapTop - 22, 0xFFFFFF);
        guiGraphics.drawCenteredString(minecraft.font, CHUNKS_LABEL.copy().append(": " + AtlasData.getDiscoveredCount(atlasStack)), centerX, mapBottom + 8, 0xD0D0D0);
        guiGraphics.drawCenteredString(minecraft.font, EMPTY_MAPS_LABEL.copy().append(": " + AtlasData.getEmptyMaps(atlasStack)), centerX, mapBottom + 20, 0xCFE8CF);
        guiGraphics.drawCenteredString(minecraft.font, CONSUMED_MAPS_LABEL.copy().append(": " + AtlasData.getConsumedMaps(atlasStack)), centerX, mapBottom + 32, 0xE8CFCF);

        if (discoveredChunks.length == 0) {
            guiGraphics.drawCenteredString(minecraft.font, EMPTY_DESCRIPTION, centerX, mapBottom + 44, 0xC0C0C0);
        }
    }

    private static ItemStack getHeldAtlas(Minecraft minecraft) {
        ItemStack mainHand = minecraft.player.getMainHandItem();
        if (mainHand.getItem() instanceof AtlasItem) {
            return mainHand;
        }

        ItemStack offHand = minecraft.player.getOffhandItem();
        if (offHand.getItem() instanceof AtlasItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
