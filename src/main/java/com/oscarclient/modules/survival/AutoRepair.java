package com.oscarclient.modules.survival;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;

/**
 * AutoRepair — Tự sửa xẻng khi độ bền xuống dưới ngưỡng.
 *
 * Chiến lược:
 *   1. Kiểm tra xẻng đang cầm mỗi 20 tick
 *   2. Nếu độ bền < config.repairAt% → tìm xẻng spare trong inventory
 *   3. Swap sang xẻng mới
 *   4. Nhắn thông báo qua action bar
 */
public class AutoRepair {

    private final OscarConfig config;
    private int checkTimer = 0;

    public AutoRepair(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        if (++checkTimer < 20) return; // kiểm tra mỗi 1 giây
        checkTimer = 0;

        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // Lấy item đang cầm
        var held = player.getMainHandStack();
        if (!(held.getItem() instanceof ToolItem)) return; // không phải tool

        int maxDmg  = held.getMaxDamage();
        int curDmg  = held.getDamage();
        // durabilityPct = % độ bền còn lại
        float durPct = maxDmg > 0 ? (1f - (float)curDmg / maxDmg) * 100f : 100f;

        if (durPct > config.repairAt) return; // Vẫn còn đủ

        // Tìm xẻng spare cùng loại trong inventory
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.main.size(); i++) {
            var stack = inventory.main.get(i);
            if (stack.getItem() == held.getItem() && stack.getDamage() == 0) {
                // Swap sang xẻng mới
                int heldSlot = inventory.selectedSlot;
                // Hoán đổi 2 slot trong hotbar
                var tmp = inventory.main.get(heldSlot).copy();
                inventory.main.set(heldSlot, stack.copy());
                inventory.main.set(i, tmp);
                ChatUtil.actionBar(mc, "§b[Oscar] ✔ Đã thay xẻng mới!");
                return;
            }
        }

        // Không có xẻng spare → cảnh báo
        ChatUtil.actionBar(mc, "§c[Oscar] ⚠ Xẻng sắp hỏng! Không có xẻng dự phòng.");
    }
}
