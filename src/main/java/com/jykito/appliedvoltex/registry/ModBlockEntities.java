package com.jykito.appliedvoltex.registry;

import com.jykito.appliedvoltex.AppliedVoltex;
import com.jykito.appliedvoltex.block.entity.AvaritiaEncoderBlockEntity;
import com.jykito.appliedvoltex.block.entity.InstantAssemblerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AppliedVoltex.MODID);

    public static final RegistryObject<BlockEntityType<InstantAssemblerBlockEntity>> INSTANT_ASSEMBLER =
            BLOCK_ENTITIES.register("instant_assembler",
                    () -> BlockEntityType.Builder.of(InstantAssemblerBlockEntity::new,
                            ModBlocks.INSTANT_ASSEMBLER.get(), ModBlocks.AVARITIA_ASSEMBLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<AvaritiaEncoderBlockEntity>> AVARITIA_ENCODER =
            BLOCK_ENTITIES.register("avaritia_encoder",
                    () -> BlockEntityType.Builder.of(AvaritiaEncoderBlockEntity::new,
                            ModBlocks.AVARITIA_ENCODER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
