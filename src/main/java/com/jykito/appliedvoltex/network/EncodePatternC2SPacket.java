package com.jykito.appliedvoltex.network;

import com.jykito.appliedvoltex.block.entity.AvaritiaEncoderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EncodePatternC2SPacket(BlockPos pos, ResourceLocation recipeId) {

    public static void encode(EncodePatternC2SPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeResourceLocation(msg.recipeId);
    }

    public static EncodePatternC2SPacket decode(FriendlyByteBuf buf) {
        return new EncodePatternC2SPacket(buf.readBlockPos(), buf.readResourceLocation());
    }

    public static void handle(EncodePatternC2SPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context c = ctx.get();
        c.enqueueWork(() -> {
            ServerPlayer sender = c.getSender();
            if (sender == null) return;
            if (sender.level().getBlockEntity(msg.pos) instanceof AvaritiaEncoderBlockEntity be
                    && sender.distanceToSqr(msg.pos.getX() + 0.5, msg.pos.getY() + 0.5, msg.pos.getZ() + 0.5) <= 64.0) {
                be.encode(msg.recipeId);
            }
        });
        c.setPacketHandled(true);
    }
}
