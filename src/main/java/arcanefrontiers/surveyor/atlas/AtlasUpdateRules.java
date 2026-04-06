package arcanefrontiers.surveyor.atlas;

import arcanefrontiers.surveyor.registry.SurveyorBlocks;
import arcanefrontiers.surveyor.world.block.TravelEaselBlock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class AtlasUpdateRules {
    private static final Map<UUID, BlockPos> LAST_POSITIONS = new HashMap<>();

    public enum UpdateStatus {
        READY,
        MOVING,
        NO_EASEL,
        MISSING_TOOLS
    }

    private AtlasUpdateRules() {
    }

    public static UpdateStatus getServerStatus(ServerPlayer player) {
        if (!isStationaryServer(player)) {
            return UpdateStatus.MOVING;
        }

        BlockPos easelPos = findNearbyEasel(player.level(), player.blockPosition());
        if (easelPos == null) {
            return UpdateStatus.NO_EASEL;
        }

        BlockState state = player.level().getBlockState(easelPos);
        if (!TravelEaselBlock.hasAllTools(state)) {
            return UpdateStatus.MISSING_TOOLS;
        }

        return UpdateStatus.READY;
    }

    public static UpdateStatus getClientStatus(Player player) {
        if (!isStationaryClient(player)) {
            return UpdateStatus.MOVING;
        }

        BlockPos easelPos = findNearbyEasel(player.level(), player.blockPosition());
        if (easelPos == null) {
            return UpdateStatus.NO_EASEL;
        }

        BlockState state = player.level().getBlockState(easelPos);
        if (!TravelEaselBlock.hasAllTools(state)) {
            return UpdateStatus.MISSING_TOOLS;
        }

        return UpdateStatus.READY;
    }

    private static boolean isStationaryServer(ServerPlayer player) {
        UUID playerId = player.getUUID();
        BlockPos currentPos = player.blockPosition();
        BlockPos previousPos = LAST_POSITIONS.put(playerId, currentPos);
        if (previousPos == null || !previousPos.equals(currentPos)) {
            return false;
        }

        return player.getDeltaMovement().horizontalDistanceSqr() <= AtlasSettings.active().maxStationaryVelocitySq();
    }

    private static boolean isStationaryClient(Player player) {
        return player.getDeltaMovement().horizontalDistanceSqr() <= AtlasSettings.active().maxStationaryVelocitySq();
    }

    private static BlockPos findNearbyEasel(Level level, BlockPos origin) {
        int easelSearchRadius = AtlasSettings.active().easelSearchRadius();
        BlockPos min = origin.offset(-easelSearchRadius, -easelSearchRadius, -easelSearchRadius);
        BlockPos max = origin.offset(easelSearchRadius, easelSearchRadius, easelSearchRadius);

        for (BlockPos scanPos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(scanPos).is(SurveyorBlocks.TRAVEL_EASEL.get())) {
                return scanPos.immutable();
            }
        }

        return null;
    }
}