package com.jykito.appliedvoltex.block.entity;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.orientation.BlockOrientation;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.client.render.effects.ParticleTypes;
import appeng.me.helpers.MachineSource;
import com.jykito.appliedvoltex.menu.InstantAssemblerMenu;
import com.jykito.appliedvoltex.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class InstantAssemblerBlockEntity extends AENetworkBlockEntity implements ICraftingProvider, IActionHost, MenuProvider {

    private static final int PATTERN_SLOTS = 36;

    private final ItemStackHandler patternInv = new ItemStackHandler(PATTERN_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            updatePatterns();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.isEmpty() || PatternDetailsHelper.isEncodedPattern(stack);
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {

            setSize(PATTERN_SLOTS);
            ListTag items = nbt.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag item = items.getCompound(i);
                int slot = item.getInt("Slot");
                if (slot >= 0 && slot < stacks.size()) {
                    stacks.set(slot, ItemStack.of(item));
                }
            }
            onLoad();
        }
    };

    private List<IPatternDetails> patterns = List.of();
    private final IActionSource actionSource = new MachineSource(this);

    private final List<GenericStack> pendingOutputs = new ArrayList<>();

    private static final int PARTICLE_ACTIVE_TICKS = 20;
    private static final int PARTICLE_INTERVAL = 2;
    private static final int PARTICLE_COUNT = 8;
    private long lastCraftTick = -1000L;
    private long lastParticleTick = -1000L;

    public InstantAssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INSTANT_ASSEMBLER.get(), pos, state);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(ICraftingProvider.class, this)
                .setIdlePowerUsage(2.0);
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.allOf(Direction.class);
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    @Override
    public IGridNode getActionableNode() {
        return getMainNode().getNode();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {

    }

    @Override
    public void onReady() {
        super.onReady();
        updatePatterns();
    }

    private void updatePatterns() {
        List<IPatternDetails> list = new ArrayList<>(PATTERN_SLOTS);
        if (level != null) {
            for (int i = 0; i < patternInv.getSlots(); i++) {
                ItemStack s = patternInv.getStackInSlot(i);
                if (s.isEmpty()) continue;
                IPatternDetails d = PatternDetailsHelper.decodePattern(s, level);
                if (d != null) list.add(d);
            }
        }
        this.patterns = list;
        ICraftingProvider.requestUpdate(getMainNode());
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return patterns;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (getMainNode().getNode() == null) return false;

        for (GenericStack out : patternDetails.getOutputs()) {
            if (out != null) pendingOutputs.add(out);
        }
        if (level != null) lastCraftTick = level.getGameTime();
        return true;
    }

    public void serverTick() {
        flushOutputs();

        if (!(level instanceof ServerLevel server)) return;
        long now = server.getGameTime();
        if (now - lastCraftTick >= PARTICLE_ACTIVE_TICKS) return;
        if (now - lastParticleTick < PARTICLE_INTERVAL) return;
        lastParticleTick = now;
        server.sendParticles(ParticleTypes.CRAFTING,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                PARTICLE_COUNT, 0.0, 0.0, 0.0, 0.0);
    }

    private void flushOutputs() {
        if (pendingOutputs.isEmpty()) return;
        var node = getMainNode().getNode();
        if (node == null) return;
        var storage = node.getGrid().getStorageService().getInventory();
        var it = pendingOutputs.listIterator();
        while (it.hasNext()) {
            GenericStack out = it.next();
            long inserted = storage.insert(out.what(), out.amount(), Actionable.MODULATE, actionSource);
            if (inserted >= out.amount()) {
                it.remove();
            } else if (inserted > 0) {
                it.set(new GenericStack(out.what(), out.amount() - inserted));
            }

        }
    }

    public ItemStackHandler getPatternInventory() {
        return patternInv;
    }

    @Override
    public Component getDisplayName() {

        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new InstantAssemblerMenu(id, playerInv, this);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        data.put("Patterns", patternInv.serializeNBT());
        if (!pendingOutputs.isEmpty()) {
            ListTag list = new ListTag();
            for (GenericStack out : pendingOutputs) {
                list.add(GenericStack.writeTag(out));
            }
            data.put("PendingOutputs", list);
        }
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        if (data.contains("Patterns")) patternInv.deserializeNBT(data.getCompound("Patterns"));
        pendingOutputs.clear();
        if (data.contains("PendingOutputs")) {
            ListTag list = data.getList("PendingOutputs", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                GenericStack gs = GenericStack.readTag(list.getCompound(i));
                if (gs != null) pendingOutputs.add(gs);
            }
        }
    }
}
