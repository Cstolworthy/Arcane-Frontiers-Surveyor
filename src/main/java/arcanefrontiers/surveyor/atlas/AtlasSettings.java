package arcanefrontiers.surveyor.atlas;

import java.util.Arrays;
import net.minecraft.nbt.CompoundTag;

public final class AtlasSettings {
    private static final String TAG_PAGE_RADIUS_CHUNKS = "PageRadiusChunks";
    private static final String TAG_ZOOM_LEVELS = "ZoomLevels";
    private static final String TAG_DEFAULT_ZOOM_INDEX = "DefaultZoomIndex";
    private static final String TAG_DISCOVERY_INTERVAL_TICKS = "DiscoveryIntervalTicks";
    private static final String TAG_FALLBACK_REVEAL_RADIUS_CHUNKS = "FallbackRevealRadiusChunks";
    private static final String TAG_EASEL_SEARCH_RADIUS = "EaselSearchRadius";
    private static final String TAG_MAX_STATIONARY_VELOCITY_SQ = "MaxStationaryVelocitySq";

    private static AtlasSettings active = defaults();

    private final int pageRadiusChunks;
    private final int[] zoomChunksPerTile;
    private final int defaultZoomIndex;
    private final int discoveryIntervalTicks;
    private final int fallbackRevealRadiusChunks;
    private final int easelSearchRadius;
    private final double maxStationaryVelocitySq;

    public AtlasSettings(
            int pageRadiusChunks,
            int[] zoomChunksPerTile,
            int defaultZoomIndex,
            int discoveryIntervalTicks,
            int fallbackRevealRadiusChunks,
            int easelSearchRadius,
            double maxStationaryVelocitySq
    ) {
        this.pageRadiusChunks = Math.max(4, pageRadiusChunks);
        this.zoomChunksPerTile = sanitizeZoomLevels(zoomChunksPerTile);
        this.defaultZoomIndex = clamp(defaultZoomIndex, 0, this.zoomChunksPerTile.length - 1);
        this.discoveryIntervalTicks = Math.max(1, discoveryIntervalTicks);
        this.fallbackRevealRadiusChunks = Math.max(1, fallbackRevealRadiusChunks);
        this.easelSearchRadius = Math.max(1, easelSearchRadius);
        this.maxStationaryVelocitySq = Math.max(0.0D, maxStationaryVelocitySq);
    }

    public static AtlasSettings active() {
        return active;
    }

    public static void setActive(AtlasSettings settings) {
        active = settings;
    }

    public static AtlasSettings defaults() {
        return new AtlasSettings(
                40,
                new int[]{1, 2, 4},
                1,
                20,
                6,
                4,
                0.0004D);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(TAG_PAGE_RADIUS_CHUNKS, pageRadiusChunks);
        tag.putIntArray(TAG_ZOOM_LEVELS, zoomChunksPerTile);
        tag.putInt(TAG_DEFAULT_ZOOM_INDEX, defaultZoomIndex);
        tag.putInt(TAG_DISCOVERY_INTERVAL_TICKS, discoveryIntervalTicks);
        tag.putInt(TAG_FALLBACK_REVEAL_RADIUS_CHUNKS, fallbackRevealRadiusChunks);
        tag.putInt(TAG_EASEL_SEARCH_RADIUS, easelSearchRadius);
        tag.putDouble(TAG_MAX_STATIONARY_VELOCITY_SQ, maxStationaryVelocitySq);
        return tag;
    }

    public static AtlasSettings fromTag(CompoundTag tag) {
        return new AtlasSettings(
                tag.contains(TAG_PAGE_RADIUS_CHUNKS) ? tag.getInt(TAG_PAGE_RADIUS_CHUNKS) : defaults().pageRadiusChunks,
                tag.contains(TAG_ZOOM_LEVELS) ? tag.getIntArray(TAG_ZOOM_LEVELS) : defaults().zoomChunksPerTile,
                tag.contains(TAG_DEFAULT_ZOOM_INDEX) ? tag.getInt(TAG_DEFAULT_ZOOM_INDEX) : defaults().defaultZoomIndex,
                tag.contains(TAG_DISCOVERY_INTERVAL_TICKS) ? tag.getInt(TAG_DISCOVERY_INTERVAL_TICKS) : defaults().discoveryIntervalTicks,
                tag.contains(TAG_FALLBACK_REVEAL_RADIUS_CHUNKS) ? tag.getInt(TAG_FALLBACK_REVEAL_RADIUS_CHUNKS) : defaults().fallbackRevealRadiusChunks,
                tag.contains(TAG_EASEL_SEARCH_RADIUS) ? tag.getInt(TAG_EASEL_SEARCH_RADIUS) : defaults().easelSearchRadius,
                tag.contains(TAG_MAX_STATIONARY_VELOCITY_SQ) ? tag.getDouble(TAG_MAX_STATIONARY_VELOCITY_SQ) : defaults().maxStationaryVelocitySq);
    }

    public int pageRadiusChunks() {
        return pageRadiusChunks;
    }

    public int pageSizeChunks() {
        return pageRadiusChunks * 2 + 1;
    }

    public int[] zoomChunksPerTile() {
        return Arrays.copyOf(zoomChunksPerTile, zoomChunksPerTile.length);
    }

    public int defaultZoomIndex() {
        return defaultZoomIndex;
    }

    public int discoveryIntervalTicks() {
        return discoveryIntervalTicks;
    }

    public int fallbackRevealRadiusChunks() {
        return fallbackRevealRadiusChunks;
    }

    public int easelSearchRadius() {
        return easelSearchRadius;
    }

    public double maxStationaryVelocitySq() {
        return maxStationaryVelocitySq;
    }

    private static int[] sanitizeZoomLevels(int[] zoomLevels) {
        if (zoomLevels == null || zoomLevels.length == 0) {
            return new int[]{1, 2, 4};
        }

        int[] sanitized = new int[zoomLevels.length];
        for (int index = 0; index < zoomLevels.length; index++) {
            sanitized[index] = Math.max(1, zoomLevels[index]);
        }
        return sanitized;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}