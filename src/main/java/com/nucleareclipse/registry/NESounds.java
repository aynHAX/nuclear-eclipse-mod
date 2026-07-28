package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Custom sound events for the seven bombs.
 *
 * <p>Each event is registered so it exists as a stable resource location.
 * The actual audio is mapped in {@code sounds.json} to a vanilla sound
 * (e.g. {@code minecraft:entity.generic.explode}) — this gives every bomb a
 * distinct, dramatic boom without requiring external .ogg assets, keeping
 * the mod fully self-contained and MIT-clean.</p>
 */
public final class NESounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NuclearEclipse.MOD_ID);

    public static final RegistryObject<SoundEvent> QUANTUM_CHARGE  = register("quantum_charge");
    public static final RegistryObject<SoundEvent> CHRONOS_WARP    = register("chronos_warp");
    public static final RegistryObject<SoundEvent> CRYSTAL_SHATTER  = register("crystal_shatter");
    public static final RegistryObject<SoundEvent> VOID_ECHO        = register("void_echo");
    public static final RegistryObject<SoundEvent> STELLAR_NOVA     = register("stellar_nova");
    public static final RegistryObject<SoundEvent> GLOW_SPORE       = register("glow_spore");
    public static final RegistryObject<SoundEvent> AURORA_HUM       = register("aurora_hum");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(NuclearEclipse.MOD_ID, name)));
    }

    private NESounds() {}
}
