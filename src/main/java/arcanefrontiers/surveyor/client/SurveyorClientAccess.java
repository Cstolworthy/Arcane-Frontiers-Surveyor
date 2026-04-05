package arcanefrontiers.surveyor.client;

public final class SurveyorClientAccess {
    private static boolean atlasOverlayOpen;

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
}
