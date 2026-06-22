package com.jykito.appliedvoltex.registry;

import com.jykito.appliedvoltex.AppliedVoltex;
import com.jykito.appliedvoltex.block.AvaritiaEncoderBlock;
import com.jykito.appliedvoltex.block.InstantAssemblerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AppliedVoltex.MODID);

    public static final RegistryObject<Block> INSTANT_ASSEMBLER = BLOCKS.register("instant_assembler",
            () -> new InstantAssemblerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final RegistryObject<Block> AVARITIA_ASSEMBLER = BLOCKS.register("avaritia_assembler",
            () -> new InstantAssemblerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final RegistryObject<Block> AVARITIA_ENCODER = BLOCKS.register("avaritia_encoder",
            () -> new AvaritiaEncoderBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
