package com.jykito.appliedvoltex.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class AssemblerItemRenderer extends BlockEntityWithoutLevelRenderer {

    public AssemblerItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        BlockState state = block.defaultBlockState();
        Minecraft mc = Minecraft.getInstance();

        mc.getBlockRenderer().renderSingleBlock(state, pose, buffer, packedLight, packedOverlay);

        BakedModel lights = ClientSetup.getLightsModel(block);
        if (lights != null && lights != mc.getModelManager().getMissingModel()) {
            ModelBlockRenderer mbr = mc.getBlockRenderer().getModelRenderer();
            VertexConsumer vc = buffer.getBuffer(RenderType.cutout());
            mbr.renderModel(pose.last(), vc, state, lights, 1f, 1f, 1f,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
        }
    }
}
