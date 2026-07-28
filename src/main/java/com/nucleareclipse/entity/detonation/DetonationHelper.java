package com.nucleareclipse.entity.detonation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Shared helpers for the seven detonations: blast radius damage, sphere
 * carving, particle spawns, and a fused "boom" sound.
 *
 * <p>All helpers are server-safe and no-op on the client.</p>
 */
public final class DetonationHelper {

    private DetonationHelper() {}

    /** Apply damage to every living entity within {@code radius} of the origin. */
    public static void damageArea(Level level, BlockPos origin, double radius, float damage,
                                  String message) {
        if (level.isClientSide) return;
        AABB box = new AABB(origin).inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : targets) {
            double dist = Math.sqrt(entity.position().distanceToSqr(
                    origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5));
            if (dist <= radius) {
                float falloff = (float) (1.0 - dist / radius);
                entity.hurt(level.damageSources().generic(), damage * (0.4F + 0.6F * falloff));
                // Knock-back radially outward from the blast center.
                double dx = entity.getX() - (origin.getX() + 0.5);
                double dy = entity.getY() - (origin.getY() + 0.5);
                double dz = entity.getZ() - (origin.getZ() + 0.5);
                double mag = Math.max(0.001, Math.sqrt(dx * dx + dy * dy + dz * dz));
                double knock = 1.2 * falloff;
                entity.push(dx / mag * knock, Math.abs(dy / mag) * knock + 0.3, dz / mag * knock);
            }
        }
    }

    /**
     * Carve a spherical crater — destroy non-bedrock blocks inside the radius.
     * Optionally drop items. Bedrock, barriers and command blocks are spared.
     */
    public static void carveSphere(Level level, BlockPos origin, double radius, boolean drop) {
        if (level.isClientSide) return;
        int r = (int) Math.ceil(radius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.isInWorldBounds(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) continue;
                    if (state.is(net.minecraft.tags.BlockTags.WITHER_IMMUNE)) continue;
                    if (drop) {
                        BlockState.dropResources(state, level, pos, level.getBlockEntity(pos));
                    }
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    /** Replace blocks in a sphere with a target block (used for scorch-glass & crystal crusts). */
    public static void crustSphere(Level level, BlockPos origin, double radius, double minRadius,
                                   BlockState crust, BlockState fallback) {
        if (level.isClientSide) return;
        int r = (int) Math.ceil(radius);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    double d = Math.sqrt(x * x + y * y + z * z);
                    if (d > radius) continue;
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!level.isInWorldBounds(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F) continue;
                    if (state.is(net.minecraft.tags.BlockTags.WITHER_IMMUNE)) continue;
                    BlockState target = (d >= minRadius) ? crust : fallback;
                    level.setBlock(pos, target, 3);
                }
            }
        }
    }

    /** Spawn a vanilla particle burst evenly around the origin (server → client broadcast). */
    public static void sendParticles(ServerLevel level, net.minecraft.core.particles.ParticleOptions type,
                                     BlockPos origin, int count, double radius, double speed) {
        for (int i = 0; i < count; i++) {
            double theta = level.random.nextDouble() * Math.PI * 2;
            double phi = level.random.nextDouble() * Math.PI;
            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.cos(phi);
            double dz = Math.sin(phi) * Math.sin(theta);
            double px = origin.getX() + 0.5 + dx * level.random.nextDouble() * radius;
            double py = origin.getY() + 0.5 + dy * level.random.nextDouble() * radius;
            double pz = origin.getZ() + 0.5 + dz * level.random.nextDouble() * radius;
            level.sendParticles(type, px, py, pz, 1,
                    dx * speed, dy * speed, dz * speed, 0.02);
        }
    }

    /** Spawn particles in a rising column (for the quantum / stellar blasts). */
    public static void sendColumn(ServerLevel level, net.minecraft.core.particles.ParticleOptions type,
                                  BlockPos origin, int count, int height, double spread) {
        for (int i = 0; i < count; i++) {
            double px = origin.getX() + 0.5 + (level.random.nextDouble() - 0.5) * spread;
            double py = origin.getY() + 0.5 + level.random.nextDouble() * height;
            double pz = origin.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * spread;
            level.sendParticles(type, px, py, pz, 1,
                    (level.random.nextDouble() - 0.5) * 0.1, 0.15 + level.random.nextDouble() * 0.1,
                    (level.random.nextDouble() - 0.5) * 0.1, 0.01);
        }
    }

    /** Play the bomb's signature sound at the blast centre. */
    public static void boom(Level level, BlockPos origin, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, origin, sound, SoundSource.BLOCKS, volume, pitch);
    }
}
