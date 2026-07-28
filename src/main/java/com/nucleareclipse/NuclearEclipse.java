package com.nucleareclipse;

import com.mojang.logging.LogUtils;
import com.nucleareclipse.registry.NEBlocks;
import com.nucleareclipse.registry.NECreativeTabs;
import com.nucleareclipse.registry.NEEntities;
import com.nucleareclipse.registry.NEItems;
import com.nucleareclipse.registry.NEParticles;
import com.nucleareclipse.registry.NESounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Nuclear Eclipse —Legendary nuclear bombs mod for Minecraft Forge 1.21.1.
 *
 * <p>Seven completely unique, never-before-seen bomb types, each with its own
 * detonation behaviour, custom particle stream and signature sound.</p>
 */
@Mod(NuclearEclipse.MOD_ID)
public final class NuclearEclipse {

    public static final String MOD_ID = "nucleareclipse";
    public static final String MOD_NAME = "Nuclear Eclipse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NuclearEclipse() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all deferred registries on the mod event bus.
        NEItems.ITEMS.register(modBus);
        NEBlocks.BLOCKS.register(modBus);
        NEEntities.ENTITIES.register(modBus);
        NEParticles.PARTICLES.register(modBus);
        NESounds.SOUNDS.register(modBus);
        NECreativeTabs.CREATIVE_MODE_TABS.register(modBus);

        // Register ourselves for server and other game events we are interested in.
        modBus.addListener(this::addCreative);
    }

    /** Populate the Nuclear Eclipse creative tab with every bomb item. */
    private void addCreative(final BuildCreativeModeTabContentsEvent event) {
        // Items are added to our own tab via NECreativeTabs; nothing extra required here.
    }
}
