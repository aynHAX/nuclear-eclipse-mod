package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Void Echo —a slow-expanding, inky ring particle that swallows light.
 * Used by the Void Echo bomb's collapsing dome.
 */
@OnlyIn(Dist.CLIENT)
public class VoidEchoParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final double startX, startY, startZ;

    protected VoidEchoParticle(ClientLevel level, double x, double y, double z,
                               double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.startX = x; this.startY = y; this.startZ = z;
        this.lifetime = 60 + random.nextInt(20);
        this.gravity = 0.0F;
        this.friction = 0.85F;
        this.rCol = 0.05F;
        this.gCol = 0.0F;
        this.bCol = 0.10F + random.nextFloat() * 0.10F;
        this.scale(2.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        // Drift outward from the start, then suck back inward.
        float t = (float) this.age / this.lifetime;
        double pull = (t > 0.6F) ? -1.5 : 1.0;
        this.x += (this.x - this.startX) * 0.02 * pull + this.xd * 0.3;
        this.y += this.yd * 0.3;
        this.z += (this.z - this.startZ) * 0.02 * pull + this.zd * 0.3;
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
        this.age++;
        this.setSpriteFromAge(sprites);
        if (this.age >= this.lifetime) this.remove();
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
            return new VoidEchoParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
