package com.oscarclient.modules.autobot;

import com.oscarclient.util.ChatUtil;
import com.oscarclient.core.OscarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnect {

    private final OscarConfig config;
    private int reconnectTimer = 0;
    private boolean waitingToReconnect = false;
    private ServerInfo lastServer = null;

    public AutoReconnect(OscarConfig config) {
        this.config = config;
    }

    public void onConnect(ServerInfo server) {
        this.lastServer = server;
    }

    public void tick(MinecraftClient mc) {
        Screen screen = mc.currentScreen;

        if (screen instanceof DisconnectedScreen) {
            if (!waitingToReconnect) {
                waitingToReconnect = true;
                reconnectTimer = 0;
                ChatUtil.LOGGER.info("[Oscar] Bị disconnect! Sẽ reconnect sau 5 giây...");
            }
            reconnectTimer++;
            if (reconnectTimer >= 100 && lastServer != null) {
                waitingToReconnect = false;
                reconnectTimer = 0;
                reconnect(mc);
            }
        } else if (screen == null) {
            waitingToReconnect = false;
            reconnectTimer = 0;
        }
    }

    private void reconnect(MinecraftClient mc) {
        if (lastServer == null) return;
        try {
            mc.execute(() -> {
                mc.setScreen(null);
                net.minecraft.client.gui.screen.ConnectScreen.connect(
                    new MultiplayerScreen(new TitleScreen()),
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
