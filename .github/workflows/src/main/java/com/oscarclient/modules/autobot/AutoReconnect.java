package com.oscarclient.modules.autobot;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

/**
 * AutoReconnect — Tự động kết nối lại KingSMP khi bị kick/disconnect.
 *
 * Cách hoạt động:
 *   1. Phát hiện DisconnectedScreen (màn hình bị kick)
 *   2. Đợi 5 giây (tránh spam)
 *   3. Tự connect lại server KingSMP
 *
 * Server address được lấy từ ServerInfo cuối cùng (server vừa chơi).
 */
public class AutoReconnect {

    private final OscarConfig config;
    private int reconnectTimer = 0;
    private boolean waitingToReconnect = false;

    // Lưu thông tin server để reconnect
    private ServerInfo lastServer = null;

    public AutoReconnect(OscarConfig config) {
        this.config = config;
    }

    /** Gọi trước khi connect server để lưu thông tin */
    public void onConnect(ServerInfo server) {
        this.lastServer = server;
    }

    public void tick(MinecraftClient mc) {
        Screen screen = mc.currentScreen;

        // Phát hiện bị kick (DisconnectedScreen)
        if (screen instanceof DisconnectedScreen) {
            if (!waitingToReconnect) {
                waitingToReconnect = true;
                reconnectTimer = 0;
                ChatUtil.LOGGER.info("[Oscar] Bị disconnect! Sẽ reconnect sau 5 giây...");
            }

            reconnectTimer++;

            // Sau 5 giây (100 tick) → reconnect
            if (reconnectTimer >= 100 && lastServer != null) {
                waitingToReconnect = false;
                reconnectTimer = 0;
                reconnect(mc);
            }
        } else if (screen == null) {
            // Đã vào game → reset
            waitingToReconnect = false;
            reconnectTimer = 0;
        }
    }

    private void reconnect(MinecraftClient mc) {
        if (lastServer == null) return;
        try {
            // Connect lại server cũ
            mc.execute(() -> {
                mc.setScreen(null);
                // Sử dụng ConnectScreen để kết nối lại
                net.minecraft.client.gui.screen.ConnectScreen.connect(
                    new MultiplayerScreen(new net.minecraft.client.gui.screen.TitleScreen()),
                    mc,
                    ServerAddress.parse(lastServer.address),
                    lastServer,
                    false,
                    null
                );
            });
            ChatUtil.LOGGER.info("[Oscar] Đang reconnect tới {}...", lastServer.address);
        } catch (Exception e) {
            ChatUtil.LOGGER.error("[Oscar] Lỗi reconnect: {}", e.getMessage());
        }
    }
}
