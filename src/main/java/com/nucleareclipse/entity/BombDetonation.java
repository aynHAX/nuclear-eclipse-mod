package com.nucleareclipse.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Strategy interface for a single bomb's detonation behaviour.
 * Implementations live in {@code com.nucleareclipse.entity.detonation}.
 */
public interface BombDetonation {

    /**
     * Perform the detonation of this bomb at the given position in the world.
     *
     * @param bomb      the bomb entity (for context, e.g. thrower)
     * @param level     the server level
     * @param origin    the impact block position
     */
    void detonate(net.minecraft.world.entity.Entity bomb, Level level, BlockPos origin);
}
