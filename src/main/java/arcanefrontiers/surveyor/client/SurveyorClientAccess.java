package arcanefrontiers.surveyor.client;

import arcanefrontiers.surveyor.atlas.AtlasSettings;

public final class SurveyorClientAccess {
    private static boolean atlasOverlayOpen;
    private static int atlasZoomIndex = AtlasSettings.active().defaultZoomIndex();

    private SurveyorClientAccess() {
    }

    public static void toggleAtlasOverlay() {
        atlasOverlayOpen = !atlasOverlayOpen;
    }

    public static boolean isAtlasOverlayOpen() {
        return atlasOverlayOpen;
    }

    public static void closeAtlasOverlay() {
        atlasOverlayOpen = false;
    }

    public static void increaseZoomLevel() {
        int[] zoomLevels = AtlasSettings.active().zoomChunksPerTile();
        atlasZoomIndex = Math.min(zoomLevels.length - 1, atlasZoomIndex + 1);
    }

    public static void decreaseZoomLevel() {
        atlasZoomIndex = Math.max(0, atlasZoomIndex - 1);
    }

    public static int getActiveZoomChunksPerTile() {
        int[] zoomLevels = AtlasSettings.active().zoomChunksPerTile();
        if (atlasZoomIndex < 0 || atlasZoomIndex >= zoomLevels.length) {
            atlasZoomIndex = AtlasSettings.active().defaultZoomIndex();
        }

        return zoomLevels[atlasZoomIndex];
    }
}
