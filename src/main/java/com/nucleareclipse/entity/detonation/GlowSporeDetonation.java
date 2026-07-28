package com.nucleareclipse.entity.detonation;

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

import java.util.List;

/**
 * <b>Glow Spore (الأبواغ المتوهجة)</b>
 *
 * <p>A bioluminescent "biological" bomb. Rather than a crater it spawns a vast
 * drifting cloud of glow-spore motes that persists for a long time. Entities
 * inside are dosed with Night Vision (so they can admire the glow), Poison
 * and Slow Falling — a beautiful but treacherous haze. The ground underneath
 * sprouts spore-blossoms and glow-lichen for a permanent luminous garden.</p>
 */
public final class GlowSporeDetonation implements BombDetonation {

    private static final double CLOUD_RADIUS = 14.0;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        DetonationHelper.boom(level, origin, NESounds.GLOW_SPORE.get(), 4.0F, 1.6F);

        // ── Thick drifting spore cloud ──
        for (int i = 0; i < 400; i++) {
            double dx = server.random.nextGaussian() * CLOUD_RADIUS;
            double dy = server.random.nextDouble() * CLOUD_RADIUS;
            double dz = server.random.nextGaussian() * CLOUD_RADIUS;
            server.sendParticles(NEParticles.GLOW_SPORE.get().get(),
                    origin.getX() + 0.5 + dx,
                    origin.getY() + 0.5 + dy,
                    origin.getZ() + 0.5 + dz, 1,
                    server.random.nextGaussian() * 0.02,
                    0.04 + server.random.nextDouble() * 0.03,
                    server.random.nextGaussian() * 0.02, 0.01);
        }

        // ── Spore-blossom + glow-lichen garden on the floor ──
        int r = (int) CLOUD_RADIUS;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z > r * r) continue;
                pos.set(origin.getX() + x, origin.getY(), origin.getZ() + z);
                if (!level.isInWorldBounds(pos)) continue;
                var floor = level.getBlockState(pos);
                if (floor.isSolidRender(level, pos)) {
                    BlockPos above = pos.above();
                    if (level.getBlockState(above).isAir() && server.random.nextFloat() < 0.25F) {
                        level.setBlock(above, Blocks.SPORE_BLOSSOM.defaultBlockState(), 3);
                    }
                    if (server.random.nextFloat() < 0.10F) {
                        level.setBlock(pos, Blocks.MOSS_BLOCK.defaultBlockState(), 3);
                    }
                }
            }
        }

        // ── Effect haze — night vision, poison, slow falling ──
        AABB box = new AABB(origin).inflate(CLOUD_RADIUS);
        List<LivingEntity> targets = server.getEntitiesOfClass(LivingEntity.class, box);
        for (LivingEntity entity : targets) {
            double dist = entity.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(origin));
            if (dist > CLOUD_RADIUS) continue;
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400, 0, false, true, true));
        }
    }
}
