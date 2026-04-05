package arcanefrontiers.surveyor.client.gui;

import arcanefrontiers.surveyor.atlas.AtlasData;
import arcanefrontiers.surveyor.item.AtlasItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;

public final class AtlasScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.surveyor.atlas.title");
    private static final Component EMPTY_DESCRIPTION = Component.translatable("screen.surveyor.atlas.empty");
    private static final Component CHUNKS_LABEL = Component.translatable("screen.surveyor.atlas.discovered_chunks");
    private static final int MAP_RADIUS_CHUNKS = 40;
    private static final int MAP_BACKGROUND = 0xA0101010;
    private static final int MAP_BORDER = 0xFFB08C5A;
    private static final int DISCOVERED_COLOR = 0xFF3D8B5A;
    private static final int PLAYER_COLOR = 0xFFFFE46A;
    private static final int SCREEN_OVERLAY = 0x70000000;

    public AtlasScreen() {
        super(TITLE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, SCREEN_OVERLAY);

        int centerX = this.width / 2;
        int maxMapSize = Math.min(this.width - 80, this.height - 140);
        int mapTileCount = MAP_RADIUS_CHUNKS * 2 + 1;
        int tileSize = Math.max(2, maxMapSize / mapTileCount);
        int mapSize = mapTileCount * tileSize;

        int mapLeft = centerX - (mapSize / 2);
        int mapTop = this.height / 2 - (mapSize / 2) + 18;
        int mapRight = mapLeft + mapSize;
        int mapBottom = mapTop + mapSize;

        guiGraphics.fill(mapLeft - 1, mapTop - 1, mapRight + 1, mapBottom + 1, MAP_BORDER);
        guiGraphics.fill(mapLeft, mapTop, mapRight, mapBottom, MAP_BACKGROUND);

        ItemStack atlasStack = getHeldAtlas();
        int playerChunkX = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.chunkPosition().x : 0;
        int playerChunkZ = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.chunkPosition().z : 0;
        int centerChunkX = AtlasData.getCenterChunkX(atlasStack, playerChunkX);
        int centerChunkZ = AtlasData.getCenterChunkZ(atlasStack, playerChunkZ);

        long[] discoveredChunks = AtlasData.getDiscoveredChunks(atlasStack);
        for (long packedChunk : discoveredChunks) {
            int chunkX = ChunkPos.getX(packedChunk);
            int chunkZ = ChunkPos.getZ(packedChunk);
            int relativeX = chunkX - centerChunkX;
            int relativeZ = chunkZ - centerChunkZ;

            if (Math.abs(relativeX) > MAP_RADIUS_CHUNKS || Math.abs(relativeZ) > MAP_RADIUS_CHUNKS) {
                continue;
            }

            int renderX = mapLeft + ((relativeX + MAP_RADIUS_CHUNKS) * tileSize);
            int renderY = mapTop + ((relativeZ + MAP_RADIUS_CHUNKS) * tileSize);
            guiGraphics.fill(renderX, renderY, renderX + tileSize, renderY + tileSize, DISCOVERED_COLOR);
        }

        int playerTileX = mapLeft + (MAP_RADIUS_CHUNKS * tileSize);
        int playerTileY = mapTop + (MAP_RADIUS_CHUNKS * tileSize);
        guiGraphics.fill(playerTileX, playerTileY, playerTileX + tileSize, playerTileY + tileSize, PLAYER_COLOR);

        guiGraphics.drawCenteredString(this.font, TITLE, centerX, mapTop - 22, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, CHUNKS_LABEL.copy().append(": " + AtlasData.getDiscoveredCount(atlasStack)), centerX, mapBottom + 8, 0xD0D0D0);

        if (discoveredChunks.length == 0) {
            guiGraphics.drawCenteredString(this.font, EMPTY_DESCRIPTION, centerX, mapBottom + 20, 0xC0C0C0);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private ItemStack getHeldAtlas() {
        if (Minecraft.getInstance().player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
        if (mainHand.getItem() instanceof AtlasItem) {
            return mainHand;
        }

        ItemStack offHand = Minecraft.getInstance().player.getOffhandItem();
        if (offHand.getItem() instanceof AtlasItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
