package com.jykito.appliedvoltex.client.screen;

import com.jykito.appliedvoltex.compat.AvaritiaRecipeBridge;
import com.jykito.appliedvoltex.compat.EncoderEntry;
import com.jykito.appliedvoltex.menu.AvaritiaEncoderMenu;
import com.jykito.appliedvoltex.network.EncodePatternC2SPacket;
import com.jykito.appliedvoltex.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AvaritiaEncoderScreen extends AbstractContainerScreen<AvaritiaEncoderMenu> {

    private static final int COLS = 7, ROWS = 4, GX = 8, GY = 34, CELL = 18;

    private List<EncoderEntry> all = List.of();
    private List<EncoderEntry> filtered = List.of();
    private int scrollRow = 0;
    private EditBox search;

    public AvaritiaEncoderScreen(AvaritiaEncoderMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft != null && minecraft.level != null && ModList.get().isLoaded("avaritia")) {
            all = AvaritiaRecipeBridge.listRecipes(minecraft.level);
        }
        search = new EditBox(font, leftPos + 8, topPos + 18, 160, 12, Component.translatable("gui.applied_voltex.search"));
        search.setMaxLength(50);
        search.setBordered(true);
        search.setResponder(this::applyFilter);
        addWidget(search);
        applyFilter(search.getValue());
    }

    private void applyFilter(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        if (q.isEmpty()) {
            filtered = all;
        } else {
            List<EncoderEntry> f = new ArrayList<>();
            for (EncoderEntry e : all) {
                if (e.output().getHoverName().getString().toLowerCase(Locale.ROOT).contains(q)) {
                    f.add(e);
                }
            }
            filtered = f;
        }
        scrollRow = 0;
    }

    private int maxScrollRow() {
        int totalRows = (filtered.size() + COLS - 1) / COLS;
        return Math.max(0, totalRows - ROWS);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        g.fill(x, y, x + imageWidth, y + 1, 0xFFFFFFFF);
        g.fill(x, y, x + 1, y + imageHeight, 0xFFFFFFFF);
        g.fill(x + imageWidth - 1, y, x + imageWidth, y + imageHeight, 0xFF555555);
        g.fill(x, y + imageHeight - 1, x + imageWidth, y + imageHeight, 0xFF555555);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                well(g, x + GX + col * CELL, y + GY + row * CELL);
            }
        }
        for (var slot : menu.slots) {
            well(g, x + slot.x - 1, y + slot.y - 1);
        }
    }

    private void well(GuiGraphics g, int sx, int sy) {
        g.fill(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B);
        g.fill(sx, sy, sx + 18, sy + 1, 0xFF373737);
        g.fill(sx, sy, sx + 1, sy + 18, 0xFF373737);
        g.fill(sx, sy + 17, sx + 18, sy + 18, 0xFFFFFFFF);
        g.fill(sx + 17, sy, sx + 18, sy + 18, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        search.render(g, mouseX, mouseY, partialTick);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scrollRow + row) * COLS + col;
                if (idx >= filtered.size()) continue;
                ItemStack out = filtered.get(idx).output();
                int ix = leftPos + GX + col * CELL + 1;
                int iy = topPos + GY + row * CELL + 1;
                g.renderItem(out, ix, iy);
                g.renderItemDecorations(font, out, ix, iy);
                if (isHovering(GX + col * CELL + 1, GY + row * CELL + 1, 16, 16, mouseX, mouseY)) {
                    g.fillGradient(ix, iy, ix + 16, iy + 16, 0x80FFFFFF, 0x80FFFFFF);
                }
            }
        }

        renderTooltip(g, mouseX, mouseY);
        EncoderEntry hovered = recipeAt(mouseX, mouseY);
        if (hovered != null) {
            g.renderTooltip(font, hovered.output(), mouseX, mouseY);
        }
    }

    @Nullable
    private EncoderEntry recipeAt(int mouseX, int mouseY) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scrollRow + row) * COLS + col;
                if (idx >= filtered.size()) continue;
                if (isHovering(GX + col * CELL + 1, GY + row * CELL + 1, 16, 16, mouseX, mouseY)) {
                    return filtered.get(idx);
                }
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        EncoderEntry e = recipeAt((int) mouseX, (int) mouseY);
        if (e != null && button == 0) {
            ModNetwork.CHANNEL.sendToServer(new EncodePatternC2SPacket(menu.getPos(), e.id()));
            if (minecraft != null) {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScrollRow() > 0) {
            scrollRow = Math.max(0, Math.min(maxScrollRow(), scrollRow - (int) Math.signum(delta)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int mods) {

        if (search.isFocused() && minecraft != null && minecraft.options.keyInventory.matches(key, scancode)) {
            return true;
        }
        return super.keyPressed(key, scancode, mods);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {

        g.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }
}
