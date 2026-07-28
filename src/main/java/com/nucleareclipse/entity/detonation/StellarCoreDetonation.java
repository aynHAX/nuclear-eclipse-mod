package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * <b>Stellar Core (قلب النجم)</b>
 *
 * <p>The most destructive bomb: a miniature supernova. A towering solar-flare
 * column erupts skyward, the ground in the blast radius is fused into magma
 * and netherrack, and a wide shockwave incinerates everything caught inside
 * (sets entities on fire, massive damage). Leaves a glowing lava lake behind.</p>
 */
public final class StellarCoreDetonation implements BombDetonation {

    private static final double CRATER_RADIUS = 12.0;
    private static final double FIRE_RADIUS    = 18.0;
    private static final int    COLUMN_HEIGHT  = 30;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        // ── Sound — solar nova ──
        DetonationHelper.boom(level, origin, NESounds.STELLAR_NOVA.get(), 12.0F, 0.8F);

        // ── Towering flare column ──
        for (int i = 0; i < 300; i++) {
            double px = origin.getX() + 0.5 + (server.random.nextDouble() - 0.5) * 8;
            double py = origin.getY() + 0.5 + server.random.nextDouble() * COLUMN_HEIGHT;
            double pz = origin.getZ() + 0.5 + (server.random.nextDouble() - 0.5) * 8;
            server.sendParticles(NEParticles.STELLAR_FLARE.get().get(),
                    px, py, pz, 1,
                    (server.random.nextDouble() - 0.5) * 0.15,
                    0.3 + server.random.nextDouble() * 0.2,
                    (server.random.nextDouble() - 0.5) * 0.15, 0.02);
        }

        // ── Surface fusion — turn crater to netherrack + magma ──
        DetonationHelper.crustSphere(level, origin, CRATER_RADIUS, CRATER_RADIUS - 2.0,
                Blocks.MAGMA_BLOCK.defaultBlockState(),
                Blocks.NETHERRACK.defaultBlockState());

        // ── Lava lake at the core ──
        int lr = (int) (CRATER_RADIUS * 0.4);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -lr; x <= lr; x++) {
            for (int z = -lr; z <= lr; z++) {
                if (x * x + z * z > lr * lr) continue;
                pos.set(origin.getX() + x, origin.getY(), origin.getZ() + z);
                if (level.isInWorldBounds(pos)) {
                    level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                }
            }
        }

        // ── Incinerating shockwave — heavy damage + fire ──
        DetonationHelper.damageArea(level, origin, FIRE_RADIUS, 30.0F, "stellar");
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(origin).inflate(FIRE_RADIUS);
        for (net.minecraft.world.entity.LivingEntity entity :
                server.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box)) {
            double dist = entity.position().distanceTo(net.minecraft.world.phys.Vec3.atCenterOf(origin));
            if (dist <= FIRE_RADIUS) entity.setRemainingFireTicks((int) (200 * (1.0 - dist / FIRE_RADIUS)));
        }

        // ── Ignite random surface blocks ──
        for (int i = 0; i < 40; i++) {
            pos.set(origin.getX() + server.random.nextInt((int)FIRE_RADIUS*2+1) - (int)FIRE_RADIUS,
                    origin.getY() + 1,
                    origin.getZ() + server.random.nextInt((int)FIRE_RADIUS*2+1) - (int)FIRE_RADIUS);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        }
    }
}
