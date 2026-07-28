package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All bomb items. Items are declared before blocks because BombItem pulls the
 * registry object directly rather than going through BlockItem.
 */
public final class NEItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NuclearEclipse.MOD_ID);

    /** Ordered map so the creative tab can be filled in a stable order. */
    public static final Map<RegistryObject<Item>, String> BOMB_ORDER = new LinkedHashMap<>();

    public static final RegistryObject<Item> QUANTUM_BOMB   = bomb("quantum_bomb");
    public static final RegistryObject<Item> CHRONOS_BOMB   = bomb("chronos_bomb");
    public static final RegistryObject<Item> CRYSTAL_BOMB   = bomb("crystal_bomb");
    public static final RegistryObject<Item> VOID_BOMB      = bomb("void_bomb");
    public static final RegistryObject<Item> STELLAR_BOMB   = bomb("stellar_bomb");
    public static final RegistryObject<Item> GLOW_SPORE_BOMB = bomb("glow_spore_bomb");
    public static final RegistryObject<Item> AURORA_BOMB    = bomb("aurora_bomb");

    // Block items for the cosmetic blocks left behind by bombs.
    public static final RegistryObject<Item> SCORCH_GLASS_ITEM =
            ITEMS.register("scorch_glass", () -> new BlockItem(NEBlocks.SCORCH_GLASS.get(),
                    new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_DEPOSIT_ITEM =
            ITEMS.register("crystal_deposit", () -> new BlockItem(NEBlocks.CRYSTAL_DEPOSIT.get(),
                    new Item.Properties()));

    private static RegistryObject<Item> bomb(String name) {
        RegistryObject<Item> obj = ITEMS.register(name,
                () -> new BombItem(new Item.Properties().stacksTo(16), name));
        BOMB_ORDER.put(obj, name);
        return obj;
    }

    private NEItems() {}
}
