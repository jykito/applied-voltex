package com.jykito.appliedvoltex.client;

import com.jykito.appliedvoltex.AppliedVoltex;
import com.jykito.appliedvoltex.client.screen.InstantAssemblerScreen;
import com.jykito.appliedvoltex.registry.ModBlockEntities;
import com.jykito.appliedvoltex.registry.ModBlocks;
import com.jykito.appliedvoltex.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = AppliedVoltex.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    public static final ResourceLocation LIGHTS_MODEL =
            new ResourceLocation(AppliedVoltex.MODID, "block/assembler_lights");
    public static final ResourceLocation AVARITIA_LIGHTS_MODEL =
            new ResourceLocation(AppliedVoltex.MODID, "block/avaritia_assembler_lights");

    @Nullable private static BakedModel lightsModel;
    @Nullable private static BakedModel avaritiaLightsModel;

    @Nullable
    public static BakedModel getLightsModel(Block block) {
        return block == ModBlocks.AVARITIA_ASSEMBLER.get() ? avaritiaLightsModel : lightsModel;
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.INSTANT_ASSEMBLER.get(), InstantAssemblerScreen::new);
            MenuScreens.register(ModMenus.AVARITIA_ENCODER.get(), com.jykito.appliedvoltex.client.screen.AvaritiaEncoderScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.INSTANT_ASSEMBLER.get(), InstantAssemblerRenderer::new);
    }

    @SubscribeEvent
    public static void registerExtraModels(ModelEvent.RegisterAdditional event) {
        event.register(LIGHTS_MODEL);
        event.register(AVARITIA_LIGHTS_MODEL);
    }

    @SubscribeEvent
    public static void onBakingCompleted(ModelEvent.BakingCompleted event) {
        lightsModel = event.getModels().get(LIGHTS_MODEL);
        avaritiaLightsModel = event.getModels().get(AVARITIA_LIGHTS_MODEL);
    }
}
