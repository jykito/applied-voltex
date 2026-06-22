package com.jykito.appliedvoltex.registry;

import com.jykito.appliedvoltex.AppliedVoltex;
import com.jykito.appliedvoltex.item.GlowingAssemblerBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AppliedVoltex.MODID);

    public static final RegistryObject<Item> INSTANT_ASSEMBLER = ITEMS.register("instant_assembler",
            () -> new GlowingAssemblerBlockItem(ModBlocks.INSTANT_ASSEMBLER.get(), new Item.Properties()));

    public static final RegistryObject<Item> AVARITIA_ASSEMBLER = ITEMS.register("avaritia_assembler",
            () -> new GlowingAssemblerBlockItem(ModBlocks.AVARITIA_ASSEMBLER.get(), new Item.Properties()));

    public static final RegistryObject<Item> AVARITIA_ENCODER = ITEMS.register("avaritia_encoder",
            () -> new BlockItem(ModBlocks.AVARITIA_ENCODER.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
