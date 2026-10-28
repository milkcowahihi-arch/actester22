package com.oscarclient.modules.autobot;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;

/**
 * AutoRespawn — Tự động bấm Hồi sinh khi chết.
 *
 * Minecraft hiển thị DeathScreen khi player chết.
 * Module này phát hiện screen đó và gửi packet PERFORM_RESPAWN.
 */
public class AutoRespawn {

    private final OscarConfig config;
    private int respawnDelay = 0;  // đợi vài tick trước khi respawn (tránh lỗi)

    public AutoRespawn(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        // Kiểm tra đang ở Death Screen không
        if (!(mc.currentScreen instanceof DeathScreen)) {
            respawnDelay = 0;
            return;
        }

        respawnDelay++;

        // Đợi 20 tick (1 giây) rồi mới respawn (tránh race condition)
        if (respawnDelay == 20) {
            if (mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(
                    new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN)
                );
                mc.setScreen(null); // Đóng Death Screen
                ChatUtil.actionBar(mc, "§a[Oscar] Đã tự hồi sinh!");
            }
        }
    }
}
