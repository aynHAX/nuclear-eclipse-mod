package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * All custom particle types for the seven bombs.
 * Each is a {@link SimpleParticleType} (no extra data) so it can be spawned
 * straight from the server via {@code level.addParticle(...)} on the client.
 */
public final class NEParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, NuclearEclipse.MOD_ID);

    public static final RegistryObject<SimpleParticleType> QUANTUM_SPARK  = register("quantum_spark");
    public static final RegistryObject<SimpleParticleType> CHRONOS_DUST   = register("chronos_dust");
    public static final RegistryObject<SimpleParticleType> CRYSTAL_SHARD  = register("crystal_shard");
    public static final RegistryObject<SimpleParticleType> VOID_ECHO      = register("void_echo");
    public static final RegistryObject<SimpleParticleType> STELLAR_FLARE = register("stellar_flare");
    public static final RegistryObject<SimpleParticleType> GLOW_SPORE    = register("glow_spore");
    public static final RegistryObject<SimpleParticleType> AURORA_RIBBON = register("aurora_ribbon");

    private static RegistryObject<SimpleParticleType> register(String name) {
        return PARTICLES.register(name, () -> new SimpleParticleType(false));
    }

    private NEParticles() {}
}
