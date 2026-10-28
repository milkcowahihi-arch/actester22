package com.oscarclient.mixin;

import com.oscarclient.core.OscarClient;
import com.oscarclient.core.OscarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinInGameHud — Vẽ Oscar Client HUD overlay lên màn hình trong game.
 *
 * HUD hiển thị (góc trên phải):
 *   ⬡ Oscar Client v1.0
 *   ● ĐANG BẬT / ○ ĐANG TẮT
 *   Cát: 0 | Tiền: 0$
 *   Chế độ: Tự đào | Nuker: ON
 *   Thời gian: 0:00:00
 */
@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        OscarClient oscar = OscarClient.getInstance();
        if (oscar == null) return;

        OscarConfig cfg = oscar.config;
        boolean running = oscar.isRunning();

        // Lấy kích thước màn hình
        int screenW = mc.getWindow().getScaledWidth();
        int y = 4;  // bắt đầu từ y=4 (góc trên)

        // ── Header ──
        drawText(mc, matrices, "§b§l⬡ Oscar Client §8v" + OscarClient.VERSION, screenW, y, true);
        y += 10;

        // ── Trạng thái ──
        String status = running ? "§a● ĐANG BẬT" : "§c○ ĐANG TẮT";
        drawText(mc, matrices, status, screenW, y, true);
        y += 10;

        // ── Thống kê (chỉ hiện khi đang bật) ──
        if (running && oscar.sandMiner != null) {
            int sand   = oscar.sandMiner.sandMined;
            double mon = oscar.sandMiner.moneyEarned;
            drawText(mc, matrices,
                "§7Cát: §f" + sand + " §7| §7Tiền: §a" + String.format("%.1f", mon) + "$",
                screenW, y, true);
            y += 10;

            // Chế độ đào
            String[] modeNames = {"Tự đào", "Giữ phím", "Nuker", "Hybrid"};
            String modeName = cfg.miningMode < modeNames.length ? modeNames[cfg.miningMode] : "?";
            drawText(mc, matrices,
                "§7Chế độ: §e" + modeName + " §8| §7Nuker: " + (cfg.nuker ? "§aON" : "§cOFF"),
                screenW, y, true);
            y += 10;

            // Trạng thái module đào
            String stateStr = oscar.sandMiner.getState().name()
                .replace("_", " ").toLowerCase();
            stateStr = Character.toUpperCase(stateStr.charAt(0)) + stateStr.substring(1);
            drawText(mc, matrices, "§7Bot: §f" + stateStr, screenW, y, true);
            y += 10;
        }

        // ── Phím tắt ──
        drawText(mc, matrices, "§8[INSERT] Mở GUI  [HOME] Bật/Tắt", screenW, y, true);
    }

    /**
     * Vẽ text lên màn hình.
     * @param rightAlign true = căn phải, false = căn trái
     */
    private void drawText(MinecraftClient mc, MatrixStack matrices,
                          String text, int screenW, int y, boolean rightAlign) {
        var renderer = mc.textRenderer;
        var textObj  = Text.literal(text);
        int x = rightAlign
            ? screenW - renderer.getWidth(textObj) - 4
            : 4;

        // Vẽ bóng text (shadow) để dễ đọc
        renderer.drawWithShadow(matrices, textObj, x, y, 0xFFFFFFFF);
    }
}
