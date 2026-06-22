package com.jykito.appliedvoltex.block.entity;

import appeng.core.definitions.AEItems;
import com.jykito.appliedvoltex.compat.AvaritiaRecipeBridge;
import com.jykito.appliedvoltex.menu.AvaritiaEncoderMenu;
import com.jykito.appliedvoltex.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class AvaritiaEncoderBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler blankInv = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.isEmpty() || stack.is(AEItems.BLANK_PATTERN.asItem());
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler outputInv = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public AvaritiaEncoderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AVARITIA_ENCODER.get(), pos, state);
    }

    public ItemStackHandler getBlankInv() {
        return blankInv;
    }

    public ItemStackHandler getOutputInv() {
        return outputInv;
    }

    public void encode(ResourceLocation recipeId) {
        if (level == null || level.isClientSide) return;
        if (!ModList.get().isLoaded("avaritia")) return;
        if (blankInv.getStackInSlot(0).isEmpty()) return;
        if (!outputInv.getStackInSlot(0).isEmpty()) return;
        ItemStack encoded = AvaritiaRecipeBridge.encode(level, recipeId);
        if (encoded.isEmpty()) return;
        blankInv.extractItem(0, 1, false);
        outputInv.setStackInSlot(0, encoded);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new AvaritiaEncoderMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Blank", blankInv.serializeNBT());
        tag.put("Output", outputInv.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Blank")) blankInv.deserializeNBT(tag.getCompound("Blank"));
        if (tag.contains("Output")) outputInv.deserializeNBT(tag.getCompound("Output"));
    }
}
