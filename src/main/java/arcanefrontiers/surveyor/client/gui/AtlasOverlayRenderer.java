package arcanefrontiers.surveyor.client.gui;

import arcanefrontiers.surveyor.atlas.AtlasData;
import arcanefrontiers.surveyor.atlas.AtlasMapLayout;
import arcanefrontiers.surveyor.atlas.AtlasUpdateRules;
import arcanefrontiers.surveyor.client.SurveyorClientAccess;
import arcanefrontiers.surveyor.client.SurveyorKeyMappings;
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
    private static final Component UPDATE_STATUS_READY = Component.translatable("screen.surveyor.atlas.update_status.ready");
    private static final Component UPDATE_STATUS_MOVING = Component.translatable("screen.surveyor.atlas.update_status.moving");
    private static final Component UPDATE_STATUS_NO_EASEL = Component.translatable("screen.surveyor.atlas.update_status.no_easel");
    private static final Component UPDATE_STATUS_MISSING_TOOLS = Component.translatable("screen.surveyor.atlas.update_status.missing_tools");
    private static final Component SCALE_LABEL = Component.translatable("screen.surveyor.atlas.scale");
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

        while (SurveyorKeyMappings.ATLAS_ZOOM_OUT.consumeClick()) {
            SurveyorClientAccess.decreaseZoomLevel();
        }
        while (SurveyorKeyMappings.ATLAS_ZOOM_IN.consumeClick()) {
            SurveyorClientAccess.increaseZoomLevel();
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
        int mapRadiusChunks = AtlasMapLayout.pageRadiusChunks();
        int chunksPerTile = SurveyorClientAccess.getActiveZoomChunksPerTile();
        int mapRadiusTiles = Math.max(1, mapRadiusChunks / chunksPerTile);
        int mapTileCount = mapRadiusTiles * 2 + 1;
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
        int centerChunkX = playerChunkX;
        int centerChunkZ = playerChunkZ;

        long[] discoveredChunks = AtlasData.getDiscoveredChunks(atlasStack);
        int[] discoveredColors = AtlasData.getDiscoveredChunkColors(atlasStack);
        for (int index = 0; index < discoveredChunks.length; index++) {
            long packedChunk = discoveredChunks[index];
            int chunkX = ChunkPos.getX(packedChunk);
            int chunkZ = ChunkPos.getZ(packedChunk);
            int relativeX = chunkX - centerChunkX;
            int relativeZ = chunkZ - centerChunkZ;

            if (Math.abs(relativeX) > mapRadiusChunks || Math.abs(relativeZ) > mapRadiusChunks) {
                continue;
            }

            int tileOffsetX = Math.floorDiv(relativeX, chunksPerTile);
            int tileOffsetZ = Math.floorDiv(relativeZ, chunksPerTile);

            int renderX = mapLeft + ((tileOffsetX + mapRadiusTiles) * tileSize);
            int renderY = mapTop + ((tileOffsetZ + mapRadiusTiles) * tileSize);
            int color = index < discoveredColors.length ? discoveredColors[index] : DISCOVERED_FALLBACK_COLOR;
            guiGraphics.fill(renderX, renderY, renderX + tileSize, renderY + tileSize, color);
        }

        int playerTileX = mapLeft + (mapRadiusTiles * tileSize);
        int playerTileY = mapTop + (mapRadiusTiles * tileSize);
        guiGraphics.fill(playerTileX, playerTileY, playerTileX + tileSize, playerTileY + tileSize, PLAYER_COLOR);

        guiGraphics.drawCenteredString(minecraft.font, TITLE, centerX, mapTop - 22, 0xFFFFFF);
        guiGraphics.drawCenteredString(minecraft.font, CHUNKS_LABEL.copy().append(": " + AtlasData.getDiscoveredCount(atlasStack)), centerX, mapBottom + 8, 0xD0D0D0);
        guiGraphics.drawCenteredString(minecraft.font, EMPTY_MAPS_LABEL.copy().append(": " + AtlasData.getEmptyMaps(atlasStack)), centerX, mapBottom + 20, 0xCFE8CF);
        guiGraphics.drawCenteredString(minecraft.font, CONSUMED_MAPS_LABEL.copy().append(": " + AtlasData.getConsumedMaps(atlasStack)), centerX, mapBottom + 32, 0xE8CFCF);
        guiGraphics.drawCenteredString(minecraft.font, getStatusText(minecraft), centerX, mapBottom + 44, 0xC0C0C0);
        guiGraphics.drawCenteredString(minecraft.font, SCALE_LABEL.copy().append(": " + chunksPerTile + "x"), centerX, mapTop - 10, 0xD9D1BF);

        if (discoveredChunks.length == 0) {
            guiGraphics.drawCenteredString(minecraft.font, EMPTY_DESCRIPTION, centerX, mapBottom + 56, 0xC0C0C0);
        }
    }

    private static Component getStatusText(Minecraft minecraft) {
        AtlasUpdateRules.UpdateStatus status = AtlasUpdateRules.getClientStatus(minecraft.player);
        return switch (status) {
            case READY -> UPDATE_STATUS_READY;
            case MOVING -> UPDATE_STATUS_MOVING;
            case NO_EASEL -> UPDATE_STATUS_NO_EASEL;
            case MISSING_TOOLS -> UPDATE_STATUS_MISSING_TOOLS;
        };
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
