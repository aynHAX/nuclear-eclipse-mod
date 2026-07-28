package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * The single "Nuclear Eclipse" creative tab containing every bomb item,
 * in the order they were declared in {@link NEItems}.
 */
public final class NECreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB,
                    NuclearEclipse.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NUCLEAR_ECLIPSE_TAB =
            CREATIVE_MODE_TABS.register("nuclear_eclipse_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nucleareclipse"))
                    .icon(() -> new ItemStack(NEItems.STELLAR_BOMB.get()))
                    .displayItems((params, output) -> {
                        NEItems.BOMB_ORDER.keySet().forEach(item -> output.accept(item.get()));
                        output.accept(NEItems.SCORCH_GLASS_ITEM.get());
                        output.accept(NEItems.CRYSTAL_DEPOSIT_ITEM.get());
                    })
                    .build());

    private NECreativeTabs() {}
}
