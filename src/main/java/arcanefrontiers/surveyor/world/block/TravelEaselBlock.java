package arcanefrontiers.surveyor.world.block;

import arcanefrontiers.surveyor.registry.SurveyorItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class TravelEaselBlock extends Block {
    public static final MapCodec<TravelEaselBlock> CODEC = simpleCodec(TravelEaselBlock::new);
    public static final BooleanProperty HAS_SEXTANT = BooleanProperty.create("has_sextant");
    public static final BooleanProperty HAS_COMPASS = BooleanProperty.create("has_compass");
    public static final BooleanProperty HAS_QUILL = BooleanProperty.create("has_quill");

    public TravelEaselBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(HAS_SEXTANT, false)
                .setValue(HAS_COMPASS, false)
                .setValue(HAS_QUILL, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            net.minecraft.world.InteractionHand hand,
            BlockHitResult hitResult
    ) {
        Item item = stack.getItem();
        BlockState updatedState = state;

        if (item == SurveyorItems.SEXTANT.get() && !state.getValue(HAS_SEXTANT)) {
            updatedState = updatedState.setValue(HAS_SEXTANT, true);
        } else if (item == net.minecraft.world.item.Items.COMPASS && !state.getValue(HAS_COMPASS)) {
            updatedState = updatedState.setValue(HAS_COMPASS, true);
        } else if (item == SurveyorItems.PEN_QUILL.get() && !state.getValue(HAS_QUILL)) {
            updatedState = updatedState.setValue(HAS_QUILL, true);
        } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, updatedState, 3);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!player.isShiftKeyDown() || !hasAnyTools(state)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            if (state.getValue(HAS_SEXTANT)) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(SurveyorItems.SEXTANT.get()));
            }
            if (state.getValue(HAS_COMPASS)) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(net.minecraft.world.item.Items.COMPASS));
            }
            if (state.getValue(HAS_QUILL)) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(SurveyorItems.PEN_QUILL.get()));
            }

            level.setBlock(pos, state
                    .setValue(HAS_SEXTANT, false)
                    .setValue(HAS_COMPASS, false)
                    .setValue(HAS_QUILL, false), 3);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public static boolean hasAllTools(BlockState state) {
        return state.getValue(HAS_SEXTANT)
                && state.getValue(HAS_COMPASS)
                && state.getValue(HAS_QUILL);
    }

    private static boolean hasAnyTools(BlockState state) {
        return state.getValue(HAS_SEXTANT)
                || state.getValue(HAS_COMPASS)
                || state.getValue(HAS_QUILL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_SEXTANT, HAS_COMPASS, HAS_QUILL);
    }
}