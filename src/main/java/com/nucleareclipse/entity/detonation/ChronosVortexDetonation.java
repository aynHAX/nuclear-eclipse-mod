package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.entity.BombDetonation;

import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * <b>Chronos Vortex (دوامة الزمن)</b>
 *
 * <p>Releases a chronon shockwave that locks every entity within the vortex
 * in a temporal stasis: Heavy Slowness, Mining Fatigue, Weakness and Levitation
 * combine to make victims drift helplessly while golden time-dust rains down.
 * The crater is left as smooth sandstone — grains of frozen time.</p>
 */
public final class ChronosVortexDetonation implements BombDetonation {

    private static final double RADIUS = 16.0;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        DetonationHelper.boom(level, origin, NESounds.CHRONOS_WARP.get(), 8.0F, 0.4F);

        // ── Slow swirling particle dome ──
        Vec3 centre = Vec3.atCenterOf(origin);
        for (int ring = 0; ring < 4; ring++) {
            double r = RADIUS * (0.3 + 0.175 * ring);
            int n = (int) (40 + 30 * ring);
            for (int j = 0; j < n; j++) {
                double angle = server.random.nextDouble() * Math.PI * 2;
                double yOff = (server.random.nextDouble() - 0.5) * RADIUS;
                double px = centre.x + Math.cos(angle) * r;
                double py = centre.y + yOff;
                double pz = centre.z + Math.sin(angle) * r;
                server.sendParticles(NEParticles.CHRONOS_DUST.get(),
                        px, py, pz, 1,
                        -Math.sin(angle) * 0.1, 0.0, Math.cos(angle) * 0.1, 0.001);
            }
        }

        // ── Temporal stasis — lock entities in place ──
        AABB box = new AABB(origin).inflate(RADIUS);
        List<LivingEntity> targets = server.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : targets) {
            double dist = entity.position().distanceTo(centre);
            int duration = (int) (200 * (1.0 - dist / RADIUS)); // up to 10s
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 9, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 4, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, duration / 2, 0, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0, false, true, true));
            entity.setDeltaMovement(entity.getDeltaMovement().scale(0.05));
        }

        // ── Crater — frozen-time sandstone floor ──
        DetonationHelper.carveSphere(level, origin, 5.0, false);
        DetonationHelper.crustSphere(level, origin, 5.0, 0,
                Blocks.SMOOTH_SANDSTONE.defaultBlockState(),
                Blocks.SMOOTH_SANDSTONE.defaultBlockState());
    }
}
