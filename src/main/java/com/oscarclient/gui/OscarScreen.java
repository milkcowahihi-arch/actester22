package com.oscarclient.gui;

import com.oscarclient.core.OscarClient;
import com.oscarclient.core.OscarConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OscarScreen extends Screen {

    private static final int W = 360;
    private static final int H = 260;

    private Tab currentTab = Tab.MAIN;
    private enum Tab { MAIN, MINING, SURVIVAL, SAFETY, AUTO, STATS }

    private int winX, winY;

    public OscarScreen() {
        super(Text.literal("Oscar Client"));
    }

    @Override
    protected void init() {
        winX = (this.width  - W) / 2;
        winY = (this.height - H) / 2;
        clearChildren();
        buildWidgets();
    }

    private void buildWidgets() {
        OscarConfig cfg = OscarClient.getInstance().config;

        addDrawableChild(ButtonWidget.builder(Text.literal("✕ Đóng"), btn -> close())
            .dimensions(winX + W - 70, winY + 6, 64, 18).build());

        String[] tabLabels = {"Chính","Đào cát","Sinh tồn","An toàn","Tự đào","Thống kê"};
        Tab[]    tabVals   = Tab.values();
        for (int i = 0; i < tabLabels.length; i++) {
            final Tab t = tabVals[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(tabLabels[i]), btn -> {
                currentTab = t; init();
            }).dimensions(winX + 4, winY + 28 + i * 22, 80, 18).build());
        }

        int cx = winX + 92;
        int cy = winY + 30;
        int bw = W - 96;

        switch (currentTab) {
            case MAIN     -> buildMainTab(cx, cy, bw, cfg);
            case MINING   -> buildMiningTab(cx, cy, bw, cfg);
            case SURVIVAL -> buildSurvivalTab(cx, cy, bw, cfg);
            case SAFETY   -> buildSafetyTab(cx, cy, bw, cfg);
            case AUTO     -> buildAutoTab(cx, cy, bw, cfg);
            case STATS    -> buildStatsTab(cx, cy, bw, cfg);
        }
    }

    private void buildMainTab(int x, int y, int w, OscarConfig cfg) {
        boolean on = OscarClient.getInstance().isRunning();
        addDrawableChild(ButtonWidget.builder(
            Text.literal(on ? "§a⬡ ĐANG BẬT — Nhấn để TẮT" : "§c⬡ ĐANG TẮT — Nhấn để BẬT"),
            btn -> { OscarClient.getInstance().toggleRunning(); init(); }
        ).dimensions(x, y, w, 24).build()); y += 30;

        addToggleRow(x, y, w, "Nuker",         cfg.nuker,      v -> { cfg.nuker=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Auto Eat",       cfg.autoEat,    v -> { cfg.autoEat=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Auto Totem",     cfg.autoTotem,  v -> { cfg.autoTotem=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti Lava",      cfg.antiLava,   v -> { cfg.antiLava=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti AFK",       cfg.antiAfk,    v -> { cfg.antiAfk=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Auto Reconnect", cfg.autoKick,   v -> { cfg.autoKick=v; save(); }); y+=22;
    }

    private void buildMiningTab(int x, int y, int w, OscarConfig cfg) {
        String[] modes = {"0:Tự đào","1:Giữ phím","2:Chỉ Nuker","3:Hybrid"};
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Chế độ: " + modes[cfg.miningMode].split(":")[1] + " ▶"),
            btn -> { cfg.miningMode = (cfg.miningMode + 1) % 4; save(); init(); }
        ).dimensions(x, y, w, 18).build()); y += 22;

        addToggleRow(x, y, w, "Nuker",           cfg.nuker,        v -> { cfg.nuker=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Giữ phím W",      cfg.holdW,        v -> { cfg.holdW=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự giao cát",     cfg.autoDeliver,  v -> { cfg.autoDeliver=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Giao thông minh", cfg.smartDeliver, v -> { cfg.smartDeliver=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti Detect",     cfg.antiDetect,   v -> { cfg.antiDetect=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Smart Path",      cfg.smartPath,    v -> { cfg.smartPath=v; save(); }); y+=22;

        String[] pickLabels = {"Gần 4ô","Vừa 8ô","Xa 16ô","Siêu xa 24ô"};
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Nhặt cát: " + pickLabels[cfg.pickupRange] + " ▶"),
            btn -> { cfg.pickupRange = (cfg.pickupRange + 1) % 4; save(); init(); }
        ).dimensions(x, y, w, 18).build()); y += 22;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Nuker radius: " + cfg.nukerRadius + " ô   [ - / + ]"), btn -> {}
        ).dimensions(x, y, w/2 - 2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("−"), btn -> {
            if (cfg.nukerRadius > 1) { cfg.nukerRadius--; save(); init(); }
        }).dimensions(x + w/2, y, (w/2-2)/2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> {
            if (cfg.nukerRadius < 8) { cfg.nukerRadius++; save(); init(); }
        }).dimensions(x + w/2 + (w/2-2)/2 + 2, y, (w/2-2)/2, 18).build());
    }

    private void buildSurvivalTab(int x, int y, int w, OscarConfig cfg) {
        addToggleRow(x, y, w, "Tự ăn",          cfg.autoEat,    v -> { cfg.autoEat=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự mua đồ ăn",   cfg.buyFood,    v -> { cfg.buyFood=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự sửa xẻng",    cfg.autoRepair, v -> { cfg.autoRepair=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự mua bình XP",  cfg.buyXp,      v -> { cfg.buyXp=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Vứt đồ rác",     cfg.dropJunk,   v -> { cfg.dropJunk=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Cầm Totem",       cfg.autoTotem,  v -> { cfg.autoTotem=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Nước hồi máu",   cfg.healPotion, v -> { cfg.healPotion=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Kiểm tra giáp",  cfg.armorCheck, v -> { cfg.armorCheck=v; save(); }); y+=22;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Ăn khi đói ≤ " + cfg.eatAt + "/20"), btn -> {}
        ).dimensions(x, y, w/2 - 2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("−"), btn -> {
            if (cfg.eatAt > 1) { cfg.eatAt--; save(); init(); }
        }).dimensions(x + w/2, y, (w/2-2)/2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> {
            if (cfg.eatAt < 19) { cfg.eatAt++; save(); init(); }
        }).dimensions(x + w/2 + (w/2-2)/2 + 2, y, (w/2-2)/2, 18).build());
    }

    private void buildSafetyTab(int x, int y, int w, OscarConfig cfg) {
        addToggleRow(x, y, w, "Thoát nước/Lava",  cfg.antiDrown,       v -> { cfg.antiDrown=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Bắc cầu qua nước", cfg.bridge,          v -> { cfg.bridge=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Né hố & tự leo",   cfg.antiPit,         v -> { cfg.antiPit=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti Lava",         cfg.antiLava,        v -> { cfg.antiLava=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti TNT",          cfg.antiTnt,         v -> { cfg.antiTnt=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Phát hiện player",  cfg.antiPlayer,      v -> { cfg.antiPlayer=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Thoát khẩn cấp",   cfg.emergencyLogout, v -> { cfg.emergencyLogout=v; save(); }); y+=22;
    }

    private void buildAutoTab(int x, int y, int w, OscarConfig cfg) {
        addToggleRow(x, y, w, "Về nhặt đồ khi chết", cfg.recoverDeath, v -> { cfg.recoverDeath=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự hồi sinh",         cfg.autoRespawn,  v -> { cfg.autoRespawn=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Đào xuyên cát",       cfg.tunnelSand,   v -> { cfg.tunnelSand=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự vào KingSMP",      cfg.autoKick,     v -> { cfg.autoKick=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Dùng ngọc Ender",     cfg.enderPearl,   v -> { cfg.enderPearl=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Anti AFK",            cfg.antiAfk,      v -> { cfg.antiAfk=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Tự craft đồ",         cfg.autoCraft,    v -> { cfg.autoCraft=v; save(); }); y+=22;
        addToggleRow(x, y, w, "Lịch trình đào",      cfg.schedule,     v -> { cfg.schedule=v; save(); }); y+=22;
    }

    private void buildStatsTab(int x, int y, int w, OscarConfig cfg) {
        var miner = OscarClient.getInstance().sandMiner;
        addDrawableChild(ButtonWidget.builder(Text.literal("↺ Đặt lại thống kê"), btn -> {
            if (miner != null) { miner.sandMined = 0; miner.sandDelivered = 0; miner.moneyEarned = 0; }
            init();
        }).dimensions(x, y, w, 18).build()); y += 22;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Giá/cát: " + cfg.pricePerSand + " $"), btn -> {}
        ).dimensions(x, y, w/2-2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("−"), btn -> {
            if (cfg.pricePerSand > 0.5) { cfg.pricePerSand = Math.round((cfg.pricePerSand-0.5)*10.0)/10.0; save(); init(); }
        }).dimensions(x + w/2, y, (w/2-2)/2, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), btn -> {
            if (cfg.pricePerSand < 50) { cfg.pricePerSand = Math.round((cfg.pricePerSand+0.5)*10.0)/10.0; save(); init(); }
        }).dimensions(x + w/2 + (w/2-2)/2 + 2, y, (w/2-2)/2, 18).build());
    }

    private void addToggleRow(int x, int y, int w, String label, boolean current,
                              java.util.function.Consumer<Boolean> setter) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), btn -> {})
            .dimensions(x, y, w - 50, 18).build());
        addDrawableChild(ButtonWidget.builder(
            Text.literal(current ? "§aON" : "§cOFF"),
            btn -> { setter.accept(!current); init(); }
        ).dimensions(x + w - 46, y, 46, 18).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Nền cửa sổ
        context.fill(winX, winY, winX + W, winY + H, 0xCC0A0E22);

        // Viền
        drawBorder(context, winX, winY, winX + W, winY + H, 0xFF3A4080);

        // Tiêu đề
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("§b§l⬡ Oscar Client §8v" + OscarClient.VERSION + " §7— KingSMP"),
            winX + W / 2, winY + 8, 0xFFFFFF);

        // Đường kẻ ngang
        context.fill(winX, winY + 20, winX + W, winY + 21, 0xFF3A4080);
        // Đường kẻ dọc
        context.fill(winX + 88, winY + 20, winX + 89, winY + H, 0xFF3A4080);

        // Thống kê tab STATS
        if (currentTab == Tab.STATS) {
            var miner = OscarClient.getInstance().sandMiner;
            int    sand  = miner != null ? miner.sandMined     : 0;
            int    del   = miner != null ? miner.sandDelivered : 0;
            double money = miner != null ? miner.moneyEarned   : 0;
            context.drawTextWithShadow(textRenderer,
                Text.literal("§7Cát: §f" + sand + "  §7Giao: §f" + del + "  §7Tiền: §a" + String.format("%.1f$", money)),
                winX + 92, winY + 24, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBorder(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1,   y1,   x2,   y1+1, color);
        context.fill(x1,   y2-1, x2,   y2,   color);
        context.fill(x1,   y1,   x1+1, y2,   color);
        context.fill(x2-1, y1,   x2,   y2,   color);
    }

    private void save() { OscarClient.getInstance().config.save(); }

    @Override
    public boolean shouldPause() { return false; }
}
