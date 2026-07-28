package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Glow Spore —a floating bioluminescent mote that drifts on the breeze and
 * gently pulsates. Used by the Glow Spore bomb's lingering cloud.
 */
@OnlyIn(Dist.CLIENT)
public class GlowSporeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private float pulse;

    protected GlowSporeParticle(ClientLevel level, double x, double y, double z,
                                double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 100 + random.nextInt(60);
        this.gravity = 0.0F;
        this.friction = 0.98F;
        this.rCol = 0.30F + random.nextFloat() * 0.20F;
        this.gCol = 1.0F;
        this.bCol = 0.40F + random.nextFloat() * 0.30F;
        this.scale(1.3F);
        this.pulse = random.nextFloat() * ((float) Math.PI * 2F);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.pulse += 0.2F;
        // Gentle bobbing motion.
        this.yd += Math.sin(this.pulse) * 0.002;
        this.xd += Math.cos(this.pulse * 0.5) * 0.002;
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
            return new GlowSporeParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
