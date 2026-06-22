package com.jykito.appliedvoltex.menu;

import com.jykito.appliedvoltex.block.entity.InstantAssemblerBlockEntity;
import com.jykito.appliedvoltex.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class InstantAssemblerMenu extends AbstractContainerMenu {

    private static final int PATTERN_SLOTS = 36;

    @Nullable
    private final InstantAssemblerBlockEntity be;

    public InstantAssemblerMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, resolve(playerInv, buf.readBlockPos()));
    }

    public InstantAssemblerMenu(int id, Inventory playerInv, @Nullable InstantAssemblerBlockEntity be) {
        super(ModMenus.INSTANT_ASSEMBLER.get(), id);
        this.be = be;

        if (be != null) {
            IItemHandler inv = be.getPatternInventory();
            for (int i = 0; i < PATTERN_SLOTS; i++) {
                addSlot(new SlotItemHandler(inv, i, 8 + (i % 9) * 18, 18 + (i / 9) * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 106 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 166));
        }
    }

    @Nullable
    private static InstantAssemblerBlockEntity resolve(Inventory playerInv, BlockPos pos) {
        return playerInv.player.level().getBlockEntity(pos) instanceof InstantAssemblerBlockEntity ia ? ia : null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (be == null || be.isRemoved()) return false;
        BlockPos p = be.getBlockPos();
        return player.distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            int invStart = PATTERN_SLOTS;
            int invEnd = slots.size();
            if (index < PATTERN_SLOTS) {

                if (!moveItemStackTo(stack, invStart, invEnd, true)) return ItemStack.EMPTY;
            } else {

                if (!moveItemStackTo(stack, 0, PATTERN_SLOTS, false)) return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }
}
