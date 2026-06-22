package com.jykito.appliedvoltex.client;

import com.jykito.appliedvoltex.block.entity.InstantAssemblerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class InstantAssemblerRenderer implements BlockEntityRenderer<InstantAssemblerBlockEntity> {

    public InstantAssemblerRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(InstantAssemblerBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        Minecraft mc = Minecraft.getInstance();
        BakedModel lights = ClientSetup.getLightsModel(state.getBlock());
        if (lights == null || lights == mc.getModelManager().getMissingModel()) return;

        ModelBlockRenderer mbr = mc.getBlockRenderer().getModelRenderer();
        VertexConsumer vc = buffer.getBuffer(RenderType.cutout());
        mbr.renderModel(pose.last(), vc, state, lights, 1f, 1f, 1f,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
    }
}
