package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Quantum Spark —a shrinking, rising spark that shifts hue over its lifetime.
 * Used by the Quantum Singularity bomb's detonation column.
 */
@OnlyIn(Dist.CLIENT)
public class QuantumSparkParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected QuantumSparkParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 40 + random.nextInt(20);
        this.gravity = -0.02F;
        this.friction = 0.96F;
        this.rCol = 0.40F + random.nextFloat() * 0.30F; // violet
        this.gCol = 0.10F + random.nextFloat() * 0.30F;
        this.bCol = 0.70F + random.nextFloat() * 0.30F;
        this.scale(1.6F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        // Hue drift toward cyan as the spark ages.
        this.rCol = Math.max(0.0F, this.rCol - 0.01F);
        this.gCol = Math.min(1.0F, this.gCol + 0.02F);
        this.bCol = Math.min(1.0F, this.bCol + 0.01F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** Provider wired up in the particle registry. */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new QuantumSparkParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
