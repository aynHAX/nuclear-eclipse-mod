package com.nucleareclipse.entity;

import com.nucleareclipse.entity.detonation.AuroraDetonation;
import com.nucleareclipse.entity.detonation.ChronosVortexDetonation;
import com.nucleareclipse.entity.detonation.CrystalShardDetonation;
import com.nucleareclipse.entity.detonation.GlowSporeDetonation;
import com.nucleareclipse.entity.detonation.QuantumSingularityDetonation;
import com.nucleareclipse.entity.detonation.StellarCoreDetonation;
import com.nucleareclipse.entity.detonation.VoidEchoDetonation;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps a bomb's "kind" string (the item registry name) to its detonation
 * strategy. Falls back to the Quantum Singularity if the kind is unknown.
 */
public final class BombDetonations {

    private static final Map<String, BombDetonation> REGISTRY = new HashMap<>();

    static {
        register("quantum_bomb",   new QuantumSingularityDetonation());
        register("chronos_bomb",   new ChronosVortexDetonation());
        register("crystal_bomb",   new CrystalShardDetonation());
        register("void_bomb",      new VoidEchoDetonation());
        register("stellar_bomb",   new StellarCoreDetonation());
        register("glow_spore_bomb", new GlowSporeDetonation());
        register("aurora_bomb",    new AuroraDetonation());
    }

    private static void register(String kind, BombDetonation detonation) {
        REGISTRY.put(kind, detonation);
    }

    public static BombDetonation byKind(String kind) {
        return REGISTRY.getOrDefault(kind, REGISTRY.get("quantum_bomb"));
    }

    private BombDetonations() {}
}
