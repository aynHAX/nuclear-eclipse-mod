package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Crystal Shard —a jagged, fast-spinning turquoise shard with additive blending.
 * Used by the Crystal Shard bomb's prismatic burst.
 */
@OnlyIn(Dist.CLIENT)
public class CrystalShardParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected CrystalShardParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 30 + random.nextInt(15);
        this.gravity = 0.05F;
        this.friction = 0.98F;
        this.rCol = 0.20F + random.nextFloat() * 0.30F;
        this.gCol = 0.85F + random.nextFloat() * 0.15F;
        this.bCol = 0.90F;
        this.scale(1.2F + random.nextFloat() * 0.6F);
        this.roll = random.nextFloat() * ((float) Math.PI * 2F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.oRoll = this.roll;
        this.roll += 0.4F;
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
            return new CrystalShardParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
