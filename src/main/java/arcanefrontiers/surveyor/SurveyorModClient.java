package arcanefrontiers.surveyor;

import arcanefrontiers.surveyor.client.gui.AtlasOverlayRenderer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SurveyorMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SurveyorMod.MOD_ID, value = Dist.CLIENT)
public final class SurveyorModClient {
    public SurveyorModClient() {
        NeoForge.EVENT_BUS.addListener(AtlasOverlayRenderer::onRenderGui);
        SurveyorMod.LOGGER.info("Surveyor client bootstrap ready");
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        SurveyorMod.LOGGER.info("Surveyor client setup for {}", Minecraft.getInstance().getUser().getName());
    }
}
