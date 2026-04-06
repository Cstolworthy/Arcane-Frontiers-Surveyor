package arcanefrontiers.surveyor.atlas;

public final class AtlasMapLayout {
    private AtlasMapLayout() {
    }

    public static int pageRadiusChunks() {
        return AtlasSettings.active().pageRadiusChunks();
    }

    public static int pageSizeChunks() {
        return AtlasSettings.active().pageSizeChunks();
    }
}
