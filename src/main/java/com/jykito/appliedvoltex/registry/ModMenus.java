package com.jykito.appliedvoltex.registry;

import com.jykito.appliedvoltex.AppliedVoltex;
import com.jykito.appliedvoltex.menu.AvaritiaEncoderMenu;
import com.jykito.appliedvoltex.menu.InstantAssemblerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, AppliedVoltex.MODID);

    public static final RegistryObject<MenuType<InstantAssemblerMenu>> INSTANT_ASSEMBLER =
            MENUS.register("instant_assembler", () -> IForgeMenuType.create(InstantAssemblerMenu::new));

    public static final RegistryObject<MenuType<AvaritiaEncoderMenu>> AVARITIA_ENCODER =
            MENUS.register("avaritia_encoder", () -> IForgeMenuType.create(AvaritiaEncoderMenu::new));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
