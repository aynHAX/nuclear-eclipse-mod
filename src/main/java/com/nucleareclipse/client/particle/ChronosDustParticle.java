package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Chronos Dust —a slow-falling golden speck that freezes mid-air, mimicking
 * a time-stutter. Used by the Chronos Vortex bomb.
 */
@OnlyIn(Dist.CLIENT)
public class ChronosDustParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private int stillTimer;

    protected ChronosDustParticle(ClientLevel level, double x, double y, double z,
                                  double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 80 + random.nextInt(40);
        this.gravity = 0.0F;
        this.friction = 0.90F;
        this.rCol = 1.0F;
        this.gCol = 0.75F + random.nextFloat() * 0.25F;
        this.bCol = 0.20F;
        this.scale(1.4F);
        this.setSpriteFromAge(sprites);
        this.stillTimer = 5 + random.nextInt(10);
    }

    @Override
    public void tick() {
        // Mimic a time-stutter: periodically freeze in place.
        if (stillTimer > 0) {
            stillTimer--;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            return;
        }
        if (random.nextFloat() < 0.15F) stillTimer = 5 + random.nextInt(8);
        super.tick();
        this.setSpriteFromAge(sprites);
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
            return new ChronosDustParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
