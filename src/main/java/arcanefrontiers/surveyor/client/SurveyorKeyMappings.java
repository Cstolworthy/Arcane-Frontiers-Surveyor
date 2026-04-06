package arcanefrontiers.surveyor.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public final class SurveyorKeyMappings {
    public static final String KEY_CATEGORY = "key.categories.surveyor";
    public static final KeyMapping ATLAS_ZOOM_OUT = new KeyMapping(
            "key.surveyor.atlas_zoom_out",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            KEY_CATEGORY);
    public static final KeyMapping ATLAS_ZOOM_IN = new KeyMapping(
            "key.surveyor.atlas_zoom_in",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            KEY_CATEGORY);

    private SurveyorKeyMappings() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ATLAS_ZOOM_OUT);
        event.register(ATLAS_ZOOM_IN);
    }
}