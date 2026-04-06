package arcanefrontiers.surveyor;

import arcanefrontiers.surveyor.atlas.AtlasDiscoveryTracker;
import arcanefrontiers.surveyor.command.SurveyorDebugCommands;
import arcanefrontiers.surveyor.registry.SurveyorBlocks;
import arcanefrontiers.surveyor.registry.SurveyorItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SurveyorMod.MOD_ID)
public final class SurveyorMod {
    public static final String MOD_ID = "surveyor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SurveyorMod(IEventBus modEventBus, ModContainer modContainer) {
        SurveyorBlocks.register(modEventBus);
        SurveyorItems.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(AtlasDiscoveryTracker::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(SurveyorDebugCommands::onRegisterCommands);
        LOGGER.info("Surveyor initialized for NeoForge {}", modContainer.getModInfo().getVersion());
    }
}
