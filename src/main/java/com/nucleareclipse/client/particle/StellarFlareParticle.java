package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Stellar Flare —a bright, fast-rising ember that flares white-hot then cools
 * to deep red. Used by the Stellar Core bomb's solar flare column.
 */
@OnlyIn(Dist.CLIENT)
public class StellarFlareParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected StellarFlareParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 50 + random.nextInt(25);
        this.gravity = -0.04F;
        this.friction = 0.94F;
        this.rCol = 1.0F;
        this.gCol = 0.85F + random.nextFloat() * 0.15F;
        this.bCol = 0.30F;
        this.scale(2.0F + random.nextFloat());
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        // Cool from white-hot to deep crimson.
        this.gCol = Math.max(0.05F, this.gCol - 0.02F);
        this.bCol = Math.max(0.0F, this.bCol - 0.015F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new StellarFlareParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
