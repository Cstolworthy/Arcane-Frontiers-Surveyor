package arcanefrontiers.surveyor.registry;

import arcanefrontiers.surveyor.SurveyorMod;
import arcanefrontiers.surveyor.world.block.TravelEaselBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SurveyorBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SurveyorMod.MOD_ID);

    public static final DeferredBlock<Block> TRAVEL_EASEL = BLOCKS.register(
            "travel_easel",
            () -> new TravelEaselBlock(BlockBehaviour.Properties.of()
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    private SurveyorBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}