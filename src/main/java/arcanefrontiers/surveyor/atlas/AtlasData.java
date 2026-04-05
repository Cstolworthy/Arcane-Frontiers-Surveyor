package arcanefrontiers.surveyor.atlas;

import java.util.Arrays;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkPos;

public final class AtlasData {
    private static final String TAG_DISCOVERED_CHUNKS = "SurveyorDiscoveredChunks";
    private static final String TAG_DISCOVERED_COLORS = "SurveyorDiscoveredChunkColors";
    private static final String TAG_CENTER_X = "SurveyorCenterChunkX";
    private static final String TAG_CENTER_Z = "SurveyorCenterChunkZ";
    private static final int MAX_STORED_CHUNKS = 262144;
    private static final int DEFAULT_CHUNK_COLOR = 0xFF3D8B5A;

    private AtlasData() {
    }

    public static boolean addDiscoveredChunk(ItemStack atlasStack, int chunkX, int chunkZ, int argbColor) {
        long packedChunk = ChunkPos.asLong(chunkX, chunkZ);
        CompoundTag tag = getCustomDataTag(atlasStack);
        long[] discoveredChunks = tag.getLongArray(TAG_DISCOVERED_CHUNKS);
        int[] discoveredColors = tag.getIntArray(TAG_DISCOVERED_COLORS);

        if (discoveredColors.length != discoveredChunks.length) {
            int[] resizedColors = new int[discoveredChunks.length];
            int copyLength = Math.min(discoveredColors.length, resizedColors.length);
            System.arraycopy(discoveredColors, 0, resizedColors, 0, copyLength);
            for (int index = copyLength; index < resizedColors.length; index++) {
                resizedColors[index] = DEFAULT_CHUNK_COLOR;
            }
            discoveredColors = resizedColors;
        }

        for (int index = 0; index < discoveredChunks.length; index++) {
            if (discoveredChunks[index] == packedChunk) {
                if (discoveredColors[index] != argbColor) {
                    discoveredColors[index] = argbColor;
                    tag.putIntArray(TAG_DISCOVERED_COLORS, discoveredColors);
                    atlasStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                }
                return false;
            }
        }

        long[] updatedChunks;
        int[] updatedColors;
        if (discoveredChunks.length >= MAX_STORED_CHUNKS) {
            updatedChunks = new long[MAX_STORED_CHUNKS];
            System.arraycopy(discoveredChunks, 1, updatedChunks, 0, MAX_STORED_CHUNKS - 1);
            updatedChunks[MAX_STORED_CHUNKS - 1] = packedChunk;

            updatedColors = new int[MAX_STORED_CHUNKS];
            System.arraycopy(discoveredColors, 1, updatedColors, 0, MAX_STORED_CHUNKS - 1);
            updatedColors[MAX_STORED_CHUNKS - 1] = argbColor;
        } else {
            updatedChunks = Arrays.copyOf(discoveredChunks, discoveredChunks.length + 1);
            updatedChunks[updatedChunks.length - 1] = packedChunk;

            updatedColors = Arrays.copyOf(discoveredColors, discoveredColors.length + 1);
            updatedColors[updatedColors.length - 1] = argbColor;
        }

        tag.putLongArray(TAG_DISCOVERED_CHUNKS, updatedChunks);
        tag.putIntArray(TAG_DISCOVERED_COLORS, updatedColors);

        if (!tag.contains(TAG_CENTER_X)) {
            tag.putInt(TAG_CENTER_X, chunkX);
            tag.putInt(TAG_CENTER_Z, chunkZ);
        }

        atlasStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return true;
    }

    public static void setCenterChunk(ItemStack atlasStack, int chunkX, int chunkZ) {
        CompoundTag tag = getCustomDataTag(atlasStack);
        tag.putInt(TAG_CENTER_X, chunkX);
        tag.putInt(TAG_CENTER_Z, chunkZ);
        atlasStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static long[] getDiscoveredChunks(ItemStack atlasStack) {
        CompoundTag tag = getCustomDataTag(atlasStack);
        return tag.getLongArray(TAG_DISCOVERED_CHUNKS);
    }

    public static int[] getDiscoveredChunkColors(ItemStack atlasStack) {
        CompoundTag tag = getCustomDataTag(atlasStack);
        int[] colors = tag.getIntArray(TAG_DISCOVERED_COLORS);
        long[] chunks = tag.getLongArray(TAG_DISCOVERED_CHUNKS);

        if (colors.length == chunks.length) {
            return colors;
        }

        int[] resizedColors = new int[chunks.length];
        int copyLength = Math.min(colors.length, resizedColors.length);
        System.arraycopy(colors, 0, resizedColors, 0, copyLength);
        for (int index = copyLength; index < resizedColors.length; index++) {
            resizedColors[index] = DEFAULT_CHUNK_COLOR;
        }
        return resizedColors;
    }

    public static int getDiscoveredCount(ItemStack atlasStack) {
        return getDiscoveredChunks(atlasStack).length;
    }

    public static int getCenterChunkX(ItemStack atlasStack, int fallbackChunkX) {
        CompoundTag tag = getCustomDataTag(atlasStack);
        if (!tag.contains(TAG_CENTER_X)) {
            return fallbackChunkX;
        }

        return tag.getInt(TAG_CENTER_X);
    }

    public static int getCenterChunkZ(ItemStack atlasStack, int fallbackChunkZ) {
        CompoundTag tag = getCustomDataTag(atlasStack);
        if (!tag.contains(TAG_CENTER_Z)) {
            return fallbackChunkZ;
        }

        return tag.getInt(TAG_CENTER_Z);
    }

    private static CompoundTag getCustomDataTag(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return customData.copyTag();
    }
}
