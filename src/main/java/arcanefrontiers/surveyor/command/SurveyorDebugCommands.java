package arcanefrontiers.surveyor.command;

import arcanefrontiers.surveyor.atlas.AtlasData;
import arcanefrontiers.surveyor.item.AtlasItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class SurveyorDebugCommands {
    private static final Component ROOT_HELP_HEADER = Component.literal("Arcane Frontiers Surveyor commands:");
    private static final Component ROOT_HELP_LINE_1 = Component.literal("- /arcanefrontiers help");
    private static final Component ROOT_HELP_LINE_2 = Component.literal("- /arcanefrontiers debug help");
    private static final Component ROOT_HELP_LINE_3 = Component.literal("- /arcanefrontiers debug revealmap <radius>");
    private static final Component DEBUG_HELP_HEADER = Component.literal("Arcane Frontiers Surveyor debug commands:");
    private static final Component DEBUG_HELP_LINE_1 = Component.literal("- /arcanefrontiers debug help");
    private static final Component DEBUG_HELP_LINE_2 = Component.literal("- /arcanefrontiers debug revealmap <radius>");

    private SurveyorDebugCommands() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(buildRootCommand("arcanefrontiers"));
        dispatcher.register(buildRootCommand("af"));
    }

        private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildRootCommand(String name) {
        return Commands.literal(name)
            .executes(SurveyorDebugCommands::sendRootHelp)
            .then(Commands.literal("help")
                .executes(SurveyorDebugCommands::sendRootHelp)
            )
            .then(Commands.literal("debug")
                .executes(SurveyorDebugCommands::sendDebugHelp)
                .then(Commands.literal("help")
                    .executes(SurveyorDebugCommands::sendDebugHelp)
                )
                .then(Commands.literal("revealmap")
                    .then(Commands.argument("radius", IntegerArgumentType.integer(1, 512))
                        .executes(SurveyorDebugCommands::revealMap)
                    )
                )
            );
        }

    private static int sendRootHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> ROOT_HELP_HEADER, false);
        source.sendSuccess(() -> ROOT_HELP_LINE_1, false);
        source.sendSuccess(() -> ROOT_HELP_LINE_2, false);
        source.sendSuccess(() -> ROOT_HELP_LINE_3, false);
        return 1;
    }

    private static int sendDebugHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> DEBUG_HELP_HEADER, false);
        source.sendSuccess(() -> DEBUG_HELP_LINE_1, false);
        source.sendSuccess(() -> DEBUG_HELP_LINE_2, false);
        return 1;
    }

    private static int revealMap(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        int radius = IntegerArgumentType.getInteger(context, "radius");

        ItemStack atlas = findHeldAtlas(player);
        if (atlas.isEmpty()) {
            source.sendFailure(Component.literal("Hold a Surveyor Atlas in your main hand or offhand."));
            return 0;
        }

        ChunkPos center = player.chunkPosition();
        AtlasData.setCenterChunk(atlas, center.x, center.z);

        int discovered = 0;
        for (int chunkX = center.x - radius; chunkX <= center.x + radius; chunkX++) {
            for (int chunkZ = center.z - radius; chunkZ <= center.z + radius; chunkZ++) {
                if (AtlasData.addDiscoveredChunk(atlas, chunkX, chunkZ)) {
                    discovered++;
                }
            }
        }

        int sideLength = radius * 2 + 1;
        int scanned = sideLength * sideLength;
        int discoveredCount = discovered;
        int totalStored = AtlasData.getDiscoveredCount(atlas);
        source.sendSuccess(() -> Component.literal("Surveyor revealmap complete: radius=" + radius + ", scanned=" + scanned + ", newly discovered=" + discoveredCount + ", total stored=" + totalStored), false);
        return discovered;
    }

    private static ItemStack findHeldAtlas(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof AtlasItem) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof AtlasItem) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }
}
