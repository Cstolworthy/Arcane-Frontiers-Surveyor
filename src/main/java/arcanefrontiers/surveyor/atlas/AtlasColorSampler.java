package arcanefrontiers.surveyor.atlas;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public final class AtlasColorSampler {
    private static final int DEFAULT_COLOR = 0xFF3D8B5A;

    private AtlasColorSampler() {
    }

    public static int sampleChunkColor(Level level, int chunkX, int chunkZ) {
        int blockX = (chunkX << 4) + 8;
        int blockZ = (chunkZ << 4) + 8;
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, blockX, blockZ) - 1;

        if (surfaceY < level.getMinBuildHeight()) {
            return DEFAULT_COLOR;
        }

        BlockPos samplePos = new BlockPos(blockX, surfaceY, blockZ);
        MapColor mapColor = level.getBlockState(samplePos).getMapColor(level, samplePos);
        if (mapColor == null || mapColor == MapColor.NONE) {
            return DEFAULT_COLOR;
        }

        return 0xFF000000 | mapColor.col;
    }
}
