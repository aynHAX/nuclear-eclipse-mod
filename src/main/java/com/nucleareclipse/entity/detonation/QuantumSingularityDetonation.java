package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.entity.BombDetonation;

import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * <b>Quantum Singularity (التفرد الكمي)</b>
 *
 * <p>Phase 1 — Warps all entities toward the centre (gravitational pull).
 * Phase 2 — Carves a perfect spherical crater inward, pulling surrounding
 *           blocks as "falling blocks" into the singularity to create a
 *           dramatic visual collapse.
 * Phase 3 — Reverses and flings everything outward with a violet flash.</p>
 *
 * <p>A dense column of quantum spark particles marks the event horizon.</p>
 */
public final class QuantumSingularityDetonation implements BombDetonation {

    private static final double PULL_RADIUS   = 14.0;
    private static final double CRATER_RADIUS =  7.0;
    private static final double FLING_RADIUS  = 18.0;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        // ── Sound ──
        DetonationHelper.boom(level, origin, NESounds.QUANTUM_CHARGE.get(), 10.0F, 0.6F);

        // ── Particle column — event horizon ──
        DetonationHelper.sendColumn(server, NEParticles.QUANTUM_SPARK.get(),
                origin, 80, 20, 6.0);

        // ── Phase 1: Inward pull — strongest at rim, fades at edges ──
        AABB pullBox = new AABB(origin).inflate(PULL_RADIUS);
        for (LivingEntity entity : server.getEntitiesOfClass(LivingEntity.class, pullBox)) {
            Vec3 pull = Vec3.atCenterOf(origin).subtract(entity.position()).normalize();
            double dist = entity.position().distanceTo(Vec3.atCenterOf(origin));
            double strength = Math.max(0.3, 2.5 * (1.0 - dist / PULL_RADIUS));
            entity.push(pull.x * strength, pull.y * strength + 0.2, pull.z * strength);
            entity.hurt(level.damageSources().generic(), (float) (6.0 * (1.0 - dist / PULL_RADIUS)));
        }

        // ── Phase 2: Carve deep crater ──
        DetonationHelper.carveSphere(level, origin, CRATER_RADIUS, false);

        // ── Phase 2b: Pull surrounding blocks inward as falling blocks ──
        for (int i = 0; i < 30; i++) {
            double angle = server.random.nextDouble() * Math.PI * 2;
            double r = CRATER_RADIUS + 1 + server.random.nextDouble() * 3;
            BlockPos rim = origin.offset(
                    (int)(Math.cos(angle) * r),
                    (int)(server.random.nextDouble() * 4 - 2),
                    (int)(Math.sin(angle) * r));
            if (!server.isInWorldBounds(rim)) continue;
            var state = server.getBlockState(rim);
            if (state.isAir() || state.getDestroySpeed(server, rim) < 0) continue;
            server.setBlock(rim, Blocks.AIR.defaultBlockState(), 3);
            FallingBlockEntity falling = FallingBlockEntity.fall(server, rim, state);
            // Aim toward origin.
            Vec3 dir = Vec3.atCenterOf(origin).subtract(Vec3.atCenterOf(rim)).normalize().scale(0.7);
            falling.setDeltaMovement(dir.add(0, 0.3, 0));
        }

        // ── Phase 3: Flings entities outward ──
        AABB flingBox = new AABB(origin).inflate(FLING_RADIUS);
        for (LivingEntity entity : server.getEntitiesOfClass(LivingEntity.class, flingBox)) {
            Vec3 away = entity.position().subtract(Vec3.atCenterOf(origin)).normalize();
            double dist = entity.position().distanceTo(Vec3.atCenterOf(origin));
            double fling = 3.0 * Math.max(0.2, 1.0 - dist / FLING_RADIUS);
            entity.push(away.x * fling, 1.2 + server.random.nextDouble() * 0.8, away.z * fling);
        }

        // ── Crater glass floor ──
        DetonationHelper.crustSphere(level, origin, CRATER_RADIUS, 0,
                Blocks.BLACK_STAINED_GLASS.defaultBlockState(),
                Blocks.OBSIDIAN.defaultBlockState());
    }
}
