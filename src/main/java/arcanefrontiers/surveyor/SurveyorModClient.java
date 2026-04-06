package arcanefrontiers.surveyor;

import arcanefrontiers.surveyor.client.gui.AtlasOverlayRenderer;
import arcanefrontiers.surveyor.client.SurveyorKeyMappings;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = SurveyorMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SurveyorMod.MOD_ID, value = Dist.CLIENT)
public final class SurveyorModClient {
    public SurveyorModClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(SurveyorKeyMappings::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(AtlasOverlayRenderer::onRenderGui);
        SurveyorMod.LOGGER.info("Surveyor client bootstrap ready for {}", modContainer.getModInfo().getVersion());
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        SurveyorMod.LOGGER.info("Surveyor client setup for {}", Minecraft.getInstance().getUser().getName());
    }
}
