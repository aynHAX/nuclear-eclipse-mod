package com.nucleareclipse.registry;

import com.nucleareclipse.NuclearEclipse;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Decorative crater blocks left behind by certain bombs (purely cosmetic).
 * Kept light-weight — they behave like bedrock-resistant stone but are pushable.
 */
public final class NEBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NuclearEclipse.MOD_ID);

    public static final RegistryObject<Block> SCORCH_GLASS = block("scorch_glass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3F)
                    .noOcclusion()
                    .sound(net.minecraft.world.level.block.SoundType.GLASS)
                    .pushReaction(PushReaction.NORMAL)));

    public static final RegistryObject<Block> CRYSTAL_DEPOSIT = block("crystal_deposit",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .sound(net.minecraft.world.level.block.SoundType.AMETHYST)
                    .pushReaction(PushReaction.NORMAL)));

    private static RegistryObject<Block> block(String name, Supplier<Block> factory) {
        return BLOCKS.register(name, factory);
    }

    private NEBlocks() {}
}
