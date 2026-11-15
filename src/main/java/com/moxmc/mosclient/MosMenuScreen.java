package com.moxmc.mosclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/** Dark, orange-accented settings menu inspired by the supplied reference image. */
public final class MosMenuScreen extends Screen {
    private static final int ORANGE = 0xFFFF7817;
    private static final int PANEL = 0xF20D1013;
    private static final int CARD = 0xFF111519;
    private static final int MUTED = 0xFF25292D;
    private static final int GREEN = 0xFF8BD12B;
    private final String[] tabs = {"All", "New", "HUD", "Hypixel", "PvP"};
    private final List<Feature> features = new ArrayList<>();
    private String activeTab = "All";
    private String search = "";
    private int scroll = 0;

    public MosMenuScreen() {
        super(Text.literal("Mos Client"));
        features.add(new Feature("Animations", "Combat animations", "PvP", true));
        features.add(new Feature("Armor Bar", "Armor display", "HUD", true));
        features.add(new Feature("Armor Status", "Armor durability", "HUD", true));
        features.add(new Feature("Attack Indicator", "Attack cooldown display", "PvP", false));
        features.add(new Feature("Autohide HUD", "Hide HUD automatically", "HUD", false));
        features.add(new Feature("Auto Perspective", "Perspective helper", "PvP", false));
        features.add(new Feature("Auto Text", "Text helper", "Hypixel", false));
        features.add(new Feature("Backups", "Configuration backups", "New", false));
        features.add(new Feature("Block Indicator", "Block information", "HUD", false));
        features.add(new Feature("Block Overlay", "Block outline overlay", "HUD", false));
        features.add(new Feature("Boss Bar", "Boss bar display", "HUD", false));
        features.add(new Feature("Brightness", "Brightness helper", "PvP", true));
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int panelW = Math.min(1120, width - 36);
        int panelH = Math.min(650, height - 36);
        int x = (width - panelW) / 2, y = (height - panelH) / 2;
        ctx.fill(0, 0, width, height, 0x9905080B);
        ctx.fill(x, y, x + panelW, y + panelH, PANEL);
        ctx.fill(x, y, x + panelW, y + 3, ORANGE);
        ctx.drawTextWithShadow(textRenderer, "MOS CLIENT", x + 22, y + 20, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "PvP MOD MENU  /  1.21.11", x + 22, y + 39, 0xFF9BA3AA);
        drawButton(ctx, x + panelW - 52, y + 15, 34, 30, "×", false);

        int tabX = x + 18, tabY = y + 72;
        for (String tab : tabs) {
            int w = tab.equals("Hypixel") ? 92 : 76;
            drawButton(ctx, tabX, tabY, w, 32, tab, activeTab.equals(tab));
            tabX += w + 8;
        }
        int searchW = Math.min(250, panelW / 4);
        ctx.fill(x + panelW - searchW - 20, tabY, x + panelW - 20, tabY + 32, MUTED);
        ctx.drawTextWithShadow(textRenderer, search.isEmpty() ? "Search mods..." : search, x + panelW - searchW - 10, tabY + 11, 0xFFB9BEC3);

        int left = x + 18, top = y + 118, gap = 12;
        int cols = panelW >= 900 ? 4 : panelW >= 620 ? 3 : 2;
        int cardW = (panelW - 36 - gap * (cols - 1)) / cols;
        int cardH = 145;
        List<Feature> visible = features.stream().filter(f -> activeTab.equals("All") || f.category.equals(activeTab))
                .filter(f -> search.isEmpty() || f.name.toLowerCase().contains(search.toLowerCase())).toList();
        ctx.enableScissor(left, top, x + panelW - 14, y + panelH - 14);
        for (int i = 0; i < visible.size(); i++) {
            Feature f = visible.get(i);
            int col = i % cols, row = i / cols;
            int cx = left + col * (cardW + gap), cy = top + row * (cardH + gap) - scroll;
            if (cy + cardH < top || cy > y + panelH) continue;
            ctx.fill(cx, cy, cx + cardW, cy + cardH, CARD);
            ctx.fill(cx, cy, cx + 3, cy + cardH, f.enabled ? GREEN : 0xFF292E33);
            ctx.drawTextWithShadow(textRenderer, f.name, cx + 13, cy + 13, 0xFFF1F3F5);
            ctx.drawTextWithShadow(textRenderer, f.category.toUpperCase(), cx + 13, cy + 31, 0xFF858D95);
            // simple geometric feature icon
            int iconX = cx + cardW / 2, iconY = cy + 74;
            ctx.fill(iconX - 18, iconY - 18, iconX + 18, iconY + 18, 0xFF22282D);
            ctx.drawCenteredTextWithShadow(textRenderer, f.name.substring(0, 1), iconX, iconY - 4, ORANGE);
            drawButton(ctx, cx + 12, cy + cardH - 35, cardW - 24, 25, f.enabled ? "Enabled" : "Disabled", f.enabled);
        }
        ctx.disableScissor();
        ctx.drawTextWithShadow(textRenderer, "RIGHT SHIFT: MENU   •   ESC: CLOSE", x + 20, y + panelH - 22, 0xFF858D95);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawButton(DrawContext ctx, int x, int y, int w, int h, String label, boolean selected) {
        ctx.fill(x, y, x + w, y + h, selected ? ORANGE : MUTED);
        ctx.drawCenteredTextWithShadow(textRenderer, label, x + w / 2, y + (h - 8) / 2, selected ? 0xFFFFFFFF : 0xFFB5BBC1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelW = Math.min(1120, width - 36), panelH = Math.min(650, height - 36);
        int x = (width - panelW) / 2, y = (height - panelH) / 2;
        if (mouseX >= x + panelW - 52 && mouseX <= x + panelW - 18 && mouseY >= y + 15 && mouseY <= y + 45) {
            close(); return true;
        }
        int tx = x + 18, ty = y + 72;
        for (String tab : tabs) {
            int w = tab.equals("Hypixel") ? 92 : 76;
            if (mouseX >= tx && mouseX <= tx + w && mouseY >= ty && mouseY <= ty + 32) { activeTab = tab; scroll = 0; return true; }
            tx += w + 8;
        }
        int left = x + 18, top = y + 118, gap = 12, cols = panelW >= 900 ? 4 : panelW >= 620 ? 3 : 2;
        int cardW = (panelW - 36 - gap * (cols - 1)) / cols, cardH = 145;
        List<Feature> visible = features.stream().filter(f -> activeTab.equals("All") || f.category.equals(activeTab))
                .filter(f -> search.isEmpty() || f.name.toLowerCase().contains(search.toLowerCase())).toList();
        for (int i = 0; i < visible.size(); i++) {
            int col = i % cols, row = i / cols;
            int cx = left + col * (cardW + gap), cy = top + row * (cardH + gap) - scroll;
            if (mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH) { visible.get(i).enabled = !visible.get(i).enabled; return true; }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, Math.min(1000, scroll - (int)(verticalAmount * 40)));
        return true;
    }

    private static final class Feature {
        final String name, description, category;
        boolean enabled;
        Feature(String name, String description, String category, boolean enabled) {
            this.name = name; this.description = description; this.category = category; this.enabled = enabled;
        }
    }
}
