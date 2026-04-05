package arcanefrontiers.surveyor.atlas;

import java.util.Arrays;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ChunkPos;

public final class AtlasData {
    private static final String TAG_DISCOVERED_CHUNKS = "SurveyorDiscoveredChunks";
    private static final String TAG_CENTER_X = "SurveyorCenterChunkX";
    private static final String TAG_CENTER_Z = "SurveyorCenterChunkZ";
    private static final int MAX_STORED_CHUNKS = 262144;

    private AtlasData() {
    }

    public static boolean addDiscoveredChunk(ItemStack atlasStack, int chunkX, int chunkZ) {
        long packedChunk = ChunkPos.asLong(chunkX, chunkZ);
        CompoundTag tag = getCustomDataTag(atlasStack);
        long[] discoveredChunks = tag.getLongArray(TAG_DISCOVERED_CHUNKS);

        for (long discoveredChunk : discoveredChunks) {
            if (discoveredChunk == packedChunk) {
                return false;
            }
        }

        long[] updatedChunks;
        if (discoveredChunks.length >= MAX_STORED_CHUNKS) {
            updatedChunks = new long[MAX_STORED_CHUNKS];
            System.arraycopy(discoveredChunks, 1, updatedChunks, 0, MAX_STORED_CHUNKS - 1);
            updatedChunks[MAX_STORED_CHUNKS - 1] = packedChunk;
        } else {
            updatedChunks = Arrays.copyOf(discoveredChunks, discoveredChunks.length + 1);
            updatedChunks[updatedChunks.length - 1] = packedChunk;
        }

        tag.putLongArray(TAG_DISCOVERED_CHUNKS, updatedChunks);

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
