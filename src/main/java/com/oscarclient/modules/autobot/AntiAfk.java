package com.oscarclient.modules.autobot;

import com.oscarclient.core.OscarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * AntiAfk — Tự động di chuyển nhỏ mỗi N giây để không bị kick AFK.
 *
 * KingSMP thường kick sau 5 phút không hoạt động.
 * Module này xoay nhẹ camera mỗi 25 giây (mặc định) để server thấy có hoạt động.
 */
public class AntiAfk {

    private final OscarConfig config;
    private int timer = 0;
    private int phase = 0;  // pha xoay (0 = trái, 1 = phải)

    public AntiAfk(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // config.antiAfkInterval giây = antiAfkInterval * 20 tick
        timer++;
        int intervalTicks = config.antiAfkInterval * 20;

        if (timer >= intervalTicks) {
            timer = 0;
            // Xoay camera nhẹ sang trái/phải luân phiên
            float delta = (phase % 2 == 0) ? 3.0f : -3.0f;
            player.setYaw(player.getYaw() + delta);
            phase++;
        }
    }
}
