package com.nucleareclipse.client;

import com.nucleareclipse.client.particle.AuroraRibbonParticle;
import com.nucleareclipse.client.particle.ChronosDustParticle;
import com.nucleareclipse.client.particle.CrystalShardParticle;
import com.nucleareclipse.client.particle.GlowSporeParticle;
import com.nucleareclipse.client.particle.QuantumSparkParticle;
import com.nucleareclipse.client.particle.StellarFlareParticle;
import com.nucleareclipse.client.particle.VoidEchoParticle;
import com.nucleareclipse.registry.NEParticles;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Wires up our custom particle factories on the client. Without this, the
 * particle types would register but never render anything.
 */
@Mod.EventBusSubscriber(modid = com.nucleareclipse.NuclearEclipse.MOD_ID,
                        value = Dist.CLIENT, bus = Bus.MOD)
public final class NEParticleFactories {

    private NEParticleFactories() {}

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NEParticles.QUANTUM_SPARK.get(),  QuantumSparkParticle.Provider::new);
        event.registerSpriteSet(NEParticles.CHRONOS_DUST.get(),   ChronosDustParticle.Provider::new);
        event.registerSpriteSet(NEParticles.CRYSTAL_SHARD.get(),  CrystalShardParticle.Provider::new);
        event.registerSpriteSet(NEParticles.VOID_ECHO.get(),       VoidEchoParticle.Provider::new);
        event.registerSpriteSet(NEParticles.STELLAR_FLARE.get(),  StellarFlareParticle.Provider::new);
        event.registerSpriteSet(NEParticles.GLOW_SPORE.get(),     GlowSporeParticle.Provider::new);
        event.registerSpriteSet(NEParticles.AURORA_RIBBON.get(),  AuroraRibbonParticle.Provider::new);
    }
}
