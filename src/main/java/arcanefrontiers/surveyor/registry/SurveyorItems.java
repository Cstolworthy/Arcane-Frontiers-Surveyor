package arcanefrontiers.surveyor.registry;

import arcanefrontiers.surveyor.SurveyorMod;
import arcanefrontiers.surveyor.item.AtlasItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SurveyorItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SurveyorMod.MOD_ID);

    public static final DeferredItem<Item> ATLAS = ITEMS.register("atlas", () -> new AtlasItem(new Item.Properties().stacksTo(1)));
        public static final DeferredItem<Item> SEXTANT = ITEMS.register("sextant", () -> new Item(new Item.Properties().stacksTo(1)));
        public static final DeferredItem<Item> PEN_QUILL = ITEMS.register("pen_quill", () -> new Item(new Item.Properties().stacksTo(1)));
        public static final DeferredItem<Item> TRAVEL_EASEL = ITEMS.register(
            "travel_easel",
            () -> new BlockItem(SurveyorBlocks.TRAVEL_EASEL.get(), new Item.Properties().stacksTo(1)));

    private SurveyorItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(SurveyorItems::addToCreativeTabs);
    }

    private static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(ATLAS);
            event.accept(SEXTANT);
            event.accept(PEN_QUILL);
            event.accept(TRAVEL_EASEL);
        }
    }
}
