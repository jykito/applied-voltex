package com.jykito.appliedvoltex.menu;

import com.jykito.appliedvoltex.block.entity.AvaritiaEncoderBlockEntity;
import com.jykito.appliedvoltex.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class AvaritiaEncoderMenu extends AbstractContainerMenu {

    private static final int PLAYER_START = 2;

    @Nullable private final AvaritiaEncoderBlockEntity be;
    private final BlockPos pos;

    public AvaritiaEncoderMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, resolve(playerInv, buf.readBlockPos()));
    }

    public AvaritiaEncoderMenu(int id, Inventory playerInv, @Nullable AvaritiaEncoderBlockEntity be) {
        super(ModMenus.AVARITIA_ENCODER.get(), id);
        this.be = be;
        this.pos = be != null ? be.getBlockPos() : BlockPos.ZERO;

        if (be != null) {
            addSlot(new SlotItemHandler(be.getBlankInv(), 0, 62, 112));
            addSlot(new SlotItemHandler(be.getOutputInv(), 0, 98, 112));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 134 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 194));
        }
    }

    @Nullable
    private static AvaritiaEncoderBlockEntity resolve(Inventory playerInv, BlockPos pos) {
        return playerInv.player.level().getBlockEntity(pos) instanceof AvaritiaEncoderBlockEntity e ? e : null;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean stillValid(Player player) {
        if (be == null || be.isRemoved()) return false;
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int playerEnd = slots.size();
            if (be != null && index < PLAYER_START) {

                if (!moveItemStackTo(stack, PLAYER_START, playerEnd, true)) return ItemStack.EMPTY;
            } else if (be != null) {

                if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }
}
