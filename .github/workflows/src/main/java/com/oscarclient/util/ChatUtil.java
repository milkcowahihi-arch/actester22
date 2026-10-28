package com.oscarclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChatUtil — Tiện ích gửi tin nhắn, lệnh, action bar.
 */
public class ChatUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger("OscarClient");

    /**
     * Gửi lệnh vào chat (không cần gõ /)
     * Ví dụ: sendCommand(mc, "orders") → gõ /orders
     */
    public static void sendCommand(MinecraftClient mc, String command) {
        if (mc.player == null) return;
        mc.player.networkHandler.sendChatCommand(command);
    }

    /**
     * Gửi tin nhắn chat bình thường
     */
    public static void sendMessage(MinecraftClient mc, String message) {
        if (mc.player == null) return;
        mc.player.networkHandler.sendChatMessage(message);
    }

    /**
     * Hiển thị thông báo trên action bar (trên thanh HP, không spam chat)
     */
    public static void actionBar(MinecraftClient mc, String text) {
        if (mc.player == null) return;
        mc.player.sendMessage(Text.literal(text), true);
    }

    /**
     * In ra console
     */
    public static void log(String msg) {
        LOGGER.info("[OscarClient] {}", msg);
    }
}
