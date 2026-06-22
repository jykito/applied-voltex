package com.jykito.appliedvoltex;

import com.jykito.appliedvoltex.network.ModNetwork;
import com.jykito.appliedvoltex.registry.ModBlockEntities;
import com.jykito.appliedvoltex.registry.ModBlocks;
import com.jykito.appliedvoltex.registry.ModCreativeTab;
import com.jykito.appliedvoltex.registry.ModItems;
import com.jykito.appliedvoltex.registry.ModMenus;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AppliedVoltex.MODID)
public class AppliedVoltex {
    public static final String MODID = "applied_voltex";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AppliedVoltex() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModCreativeTab.register(modBus);

        ModNetwork.register();

        LOGGER.info("Applied Voltex initializing — AE2 addon loaded.");
    }
}
