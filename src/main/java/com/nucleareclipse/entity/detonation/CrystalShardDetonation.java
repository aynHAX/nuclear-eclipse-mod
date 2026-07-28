package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.entity.BombDetonation;

import com.nucleareclipse.registry.NEBlocks;
import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * <b>Crystal Shard (منشور البلورة)</b>
 *
 * <p>A prismatic detonation that doesn't destroy the terrain — instead it
 * transmutes the surface into a glittering turquoise crystal crust
 * ({@link NEBlocks#CRYSTAL_DEPOSIT}) and launches a starburst of spinning
 * crystal shards. Entities caught inside are pelted with minor shrapnel
 * damage and a moderate knockback. The shards sparkle for several seconds.</p>
 */
public final class CrystalShardDetonation implements BombDetonation {

    private static final double TRANSFORM_RADIUS = 8.0;
    private static final double DAMAGE_RADIUS    = 10.0;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        DetonationHelper.boom(level, origin, NESounds.CRYSTAL_SHATTER.get(), 8.0F, 1.4F);

        // ── Prismatic shard burst (dense, fast, omni-directional) ──
        for (int i = 0; i < 200; i++) {
            double theta = server.random.nextDouble() * Math.PI * 2;
            double phi = server.random.nextDouble() * Math.PI;
            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.cos(phi);
            double dz = Math.sin(phi) * Math.sin(theta);
            double speed = 0.6 + server.random.nextDouble() * 0.8;
            server.sendParticles(NEParticles.CRYSTAL_SHARD.get(),
                    origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5, 1,
                    dx * speed, dy * speed, dz * speed, 0.05);
        }

        // ── Crystallise the terrain (no destruction) ──
        DetonationHelper.crustSphere(level, origin, TRANSFORM_RADIUS, TRANSFORM_RADIUS - 1.0,
                NEBlocks.CRYSTAL_DEPOSIT.get().defaultBlockState(),
                net.minecraft.world.level.block.Blocks.BUDDING_AMETHYST.defaultBlockState());

        // ── Scattered glowing blocks for ambient sparkle ──
        int r = (int) TRANSFORM_RADIUS;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 30; i++) {
            pos.set(origin.getX() + server.random.nextInt(r*2+1) - r,
                    origin.getY() + server.random.nextInt(r*2+1) - r,
                    origin.getZ() + server.random.nextInt(r*2+1) - r);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                level.setBlock(pos, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.defaultBlockState(), 3);
            }
        }

        // ── Damage — light shrapnel but strong knockback ──
        DetonationHelper.damageArea(level, origin, DAMAGE_RADIUS, 8.0F, "crystal");
    }
}
