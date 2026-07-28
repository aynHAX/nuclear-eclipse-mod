package com.nucleareclipse.item;

import com.nucleareclipse.entity.BombEntity;
import com.nucleareclipse.registry.NEEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Throwing item for every bomb. Right-click throws a {@link BombEntity}
 * carrying the bomb's "kind" id so the entity knows which detonation to run.
 */
public class BombItem extends Item {

    /** The semantic id of the bomb — matches the item registry name. */
    private final String bombKind;

    public BombItem(Properties properties, String bombKind) {
        super(properties);
        this.bombKind = bombKind;
    }

    public String bombKind() {
        return bombKind;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BombEntity bomb = new BombEntity(NEEntities.BOMB.get(), level);
        bomb.setPos(context.getClickLocation().x,
                    context.getClickLocation().y,
                    context.getClickLocation().z);
        bomb.setKind(bombKind);
        bomb.shootFromRotation(player, player.getXRot(), player.getYRot(),
                               0.0F, 1.6F, 0.5F);
        level.addFreshEntity(bomb);
        // Subtle warning "charge" sound on throw.
        level.playSound(null, bomb.blockPosition(),
                net.minecraft.sounds.SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.4F, 1.2F);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                 TooltipFlag flag) {
        tooltip.add(Component.translatable("item.nucleareclipse." + bombKind + ".desc")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.nucleareclipse.throw")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
