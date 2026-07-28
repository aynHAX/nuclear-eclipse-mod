package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.entity.BombDetonation;

import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * <b>Void Echo (صدى الفراغ)</b>
 *
 * <p>A silent, light-eating implosion. The blast radius is carved clean and
 * filled with darkness — every block in the inner core becomes black concrete
 * powder (a void stain), then a collapsing dome of inky void-echo particles
 * contracts back toward the centre and "swallows" itself. Entities inside
 * suffer heavy damage and are yanked toward the centre, then blinded.</p>
 */
public final class VoidEchoDetonation implements BombDetonation {

    private static final double CRATER_RADIUS  = 9.0;
    private static final double EFFECT_RADIUS  = 16.0;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        // ── Sound — a low, muffled void thud ──
        DetonationHelper.boom(level, origin, NESounds.VOID_ECHO.get(), 9.0F, 0.3F);

        // ── Particle: expanding ring that collapses inward ──
        Vec3 centre = Vec3.atCenterOf(origin);
        for (int ring = 0; ring < 6; ring++) {
            double r = EFFECT_RADIUS * (1.0 - ring * 0.15);
            for (int j = 0; j < 50; j++) {
                double angle = server.random.nextDouble() * Math.PI * 2;
                double yOff = (server.random.nextDouble() - 0.5) * EFFECT_RADIUS * 0.6;
                double px = centre.x + Math.cos(angle) * r;
                double py = centre.y + yOff;
                double pz = centre.z + Math.sin(angle) * r;
                // Velocity points inward — the ring implodes.
                server.sendParticles(NEParticles.VOID_ECHO.get(),
                        px, py, pz, 1,
                        -Math.cos(angle) * 0.3, 0.0, -Math.sin(angle) * 0.3, 0.01);
            }
        }

        // ── Carve the crater ──
        DetonationHelper.carveSphere(level, origin, CRATER_RADIUS, false);

        // ── Stain the crater floor with black void-concrete ──
        DetonationHelper.crustSphere(level, origin, CRATER_RADIUS, CRATER_RADIUS - 2.0,
                Blocks.BLACK_CONCRETE_POWDER.defaultBlockState(),
                Blocks.OBSIDIAN.defaultBlockState());

        // ── Heavy pull + damage + blindness ──
        AABB box = new AABB(origin).inflate(EFFECT_RADIUS);
        List<LivingEntity> targets = server.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : targets) {
            Vec3 toCentre = centre.subtract(entity.position());
            double dist = toCentre.length();
            double strength = Math.max(0.5, 3.0 * (1.0 - dist / EFFECT_RADIUS));
            entity.push(toCentre.normalize().x * strength,
                        Math.abs(toCentre.normalize().y) * strength + 0.3,
                        toCentre.normalize().z * strength);
            entity.hurt(level.damageSources().generic(), (float) (15.0 * (1.0 - dist / EFFECT_RADIUS)));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.DARKNESS, 200, 0, false, true, true));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.BLINDNESS, 160, 0, false, true, true));
        }
    }
}
