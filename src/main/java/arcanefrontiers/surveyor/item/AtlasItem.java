package arcanefrontiers.surveyor.item;

import arcanefrontiers.surveyor.client.SurveyorClientAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class AtlasItem extends Item {
    public AtlasItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            SurveyorClientAccess.toggleAtlasOverlay();
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
