package com.jykito.appliedvoltex.registry;

import com.jykito.appliedvoltex.AppliedVoltex;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedVoltex.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.applied_voltex"))
                    .icon(() -> new ItemStack(ModItems.INSTANT_ASSEMBLER.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.INSTANT_ASSEMBLER.get());
                        output.accept(ModItems.AVARITIA_ASSEMBLER.get());
                        output.accept(ModItems.AVARITIA_ENCODER.get());
                    })
                    .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
