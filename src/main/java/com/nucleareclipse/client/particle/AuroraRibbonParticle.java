package com.nucleareclipse.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Aurora Ribbon —a wide, slow, hue-shifting ribbon of light that shimmers
 * like the polar aurora. Used by the Aurora bomb's sky-curtain effect.
 */
@OnlyIn(Dist.CLIENT)
public class AuroraRibbonParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected AuroraRibbonParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 120 + random.nextInt(60);
        this.gravity = 0.0F;
        this.friction = 0.99F;
        this.rCol = 0.10F + random.nextFloat() * 0.20F; // green/teal base
        this.gCol = 0.90F + random.nextFloat() * 0.10F;
        this.bCol = 0.60F + random.nextFloat() * 0.40F;
        this.scale(3.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        // Drift slowly, sway horizontally.
        this.x += this.xd + Math.sin(this.age * 0.1) * 0.05;
        this.y += this.yd;
        this.z += this.zd + Math.cos(this.age * 0.1) * 0.05;
        this.xd *= this.friction;
        this.zd *= this.friction;
        this.age++;
        this.setSpriteFromAge(sprites);
        // Slowly cycle hue across green→teal→violet.
        float t = (float) this.age * 0.02F;
        this.rCol = 0.20F + 0.20F * (0.5F + 0.5F * (float) Math.sin(t));
        this.bCol = 0.60F + 0.40F * (0.5F + 0.5F * (float) Math.cos(t * 1.3F));
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
            return new AuroraRibbonParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
