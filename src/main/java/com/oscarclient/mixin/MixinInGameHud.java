package com.oscarclient.mixin;

import com.oscarclient.core.OscarClient;
import com.oscarclient.core.OscarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        OscarClient oscar = OscarClient.getInstance();
        if (oscar == null) return;

        OscarConfig cfg = oscar.config;
        boolean running = oscar.isRunning();

        int screenW = mc.getWindow().getScaledWidth();
        int y = 4;

        drawText(mc, context, "§b§l⬡ Oscar Client §8v" + OscarClient.VERSION, screenW, y, true); y += 10;
        drawText(mc, context, running ? "§a● ĐANG BẬT" : "§c○ ĐANG TẮT", screenW, y, true); y += 10;

        if (running && oscar.sandMiner != null) {
            int sand   = oscar.sandMiner.sandMined;
            double mon = oscar.sandMiner.moneyEarned;
            drawText(mc, context, "§7Cát: §f" + sand + " §7| §7Tiền: §a" + String.format("%.1f", mon) + "$", screenW, y, true); y += 10;

            String[] modeNames = {"Tự đào", "Giữ phím", "Nuker", "Hybrid"};
            String modeName = cfg.miningMode < modeNames.length ? modeNames[cfg.miningMode] : "?";
            drawText(mc, context, "§7Chế độ: §e" + modeName + " §8| §7Nuker: " + (cfg.nuker ? "§aON" : "§cOFF"), screenW, y, true); y += 10;

            String stateStr = oscar.sandMiner.getState().name().replace("_", " ").toLowerCase();
            stateStr = Character.toUpperCase(stateStr.charAt(0)) + stateStr.substring(1);
            drawText(mc, context, "§7Bot: §f" + stateStr, screenW, y, true); y += 10;
        }

        drawText(mc, context, "§8[INSERT] Mở GUI  [HOME] Bật/Tắt", screenW, y, true);
    }

    private void drawText(MinecraftClient mc, DrawContext context, String text, int screenW, int y, boolean rightAlign) {
        var renderer = mc.textRenderer;
        var textObj  = Text.literal(text);
        int x = rightAlign ? screenW - renderer.getWidth(textObj) - 4 : 4;
        context.drawTextWithShadow(renderer, textObj, x, y, 0xFFFFFFFF);
    }
}
