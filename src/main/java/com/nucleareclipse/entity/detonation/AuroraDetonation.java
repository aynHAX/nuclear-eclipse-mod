package com.nucleareclipse.entity.detonation;

import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * <b>Aurora (الشفق القطبي)</b>
 *
 * <p>A peaceful, skyward bomb. Instead of a crater it unfurls a shimmering
 * curtain of aurora-ribbon particles 20–40 blocks into the sky that slowly
 * shifts hue from green to violet over its long lifetime. The ground beneath
 * is dusted with light-blue snow and packed-ice. Entities caught in the veil
 * are gently slowed and lifted — it's a show, not a slaughter.</p>
 */
public final class AuroraDetonation implements BombDetonation {

    private static final double VEIL_RADIUS = 22.0;
    private static final int    VEIL_HEIGHT  = 40;

    @Override
    public void detonate(Entity bomb, Level level, BlockPos origin) {
        if (!(level instanceof ServerLevel server)) return;

        DetonationHelper.boom(level, origin, NESounds.AURORA_HUM.get(), 5.0F, 1.0F);

        // ── Skyward aurora curtain — wide ribbons rising ──
        for (int i = 0; i < 500; i++) {
            double angle = server.random.nextDouble() * Math.PI * 2;
            double r = server.random.nextDouble() * VEIL_RADIUS;
            double px = origin.getX() + 0.5 + Math.cos(angle) * r;
            double py = origin.getY() + 2.0 + server.random.nextDouble() * VEIL_HEIGHT;
            double pz = origin.getZ() + 0.5 + Math.sin(angle) * r;
            server.sendParticles(NEParticles.AURORA_RIBBON.get().get(),
                    px, py, pz, 1,
                    -Math.sin(angle) * 0.04, 0.08, Math.cos(angle) * 0.04, 0.01);
        }

        // ── Snow + ice dust on the floor ──
        int r = (int) (VEIL_RADIUS * 0.6);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z > r * r) continue;
                pos.set(origin.getX() + x, origin.getY(), origin.getZ() + z);
                if (!level.isInWorldBounds(pos)) continue;
                var floor = level.getBlockState(pos);
                if (floor.isSolidRender(level, pos) && !floor.is(Blocks.ICE) && !floor.is(Blocks.PACKED_ICE)) {
                    if (server.random.nextFloat() < 0.5F) {
                        level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
                    } else if (server.random.nextFloat() < 0.3F) {
                        level.setBlock(pos.above(), Blocks.LIGHT_BLUE_CARPET.defaultBlockState(), 3);
                    }
                }
            }
        }

        // ── Gentle slow + levitation on entities ──
        DetonationHelper.damageArea(level, origin, VEIL_RADIUS * 0.5, 2.0F, "aurora");
        net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(origin).inflate(VEIL_RADIUS);
        for (net.minecraft.world.entity.LivingEntity entity :
                server.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, box)) {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true, true));
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    net.minecraft.world.effect.MobEffects.LEVITATION, 60, 0, false, true, true));
        }
    }
}
