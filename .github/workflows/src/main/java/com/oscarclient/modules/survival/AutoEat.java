package com.oscarclient.modules.survival;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.util.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;

/**
 * AutoEat — Tự động ăn khi đói xuống dưới ngưỡng cài đặt.
 *
 * Cách hoạt động:
 *   1. Kiểm tra hunger level của player mỗi tick
 *   2. Nếu hunger ≤ config.eatAt → tìm thức ăn trong inventory
 *   3. Chuyển thức ăn vào hotbar slot → giữ nút ăn
 */
public class AutoEat {

    private final OscarConfig config;
    private int eatTimer = 0;          // đếm tick đang ăn
    private int prevHeldSlot = 0;      // slot đang cầm trước khi ăn

    public AutoEat(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        // hunger: 0–20 (20 = đầy, 0 = đói hết)
        int hunger = player.getHungerManager().getFoodLevel();

        // Đang ăn → tiếp tục giữ nút ăn
        if (eatTimer > 0) {
            mc.options.useKey.setPressed(true);
            eatTimer--;
            if (eatTimer == 0) {
                mc.options.useKey.setPressed(false);
                // Trả về slot cũ
                player.getInventory().selectedSlot = prevHeldSlot;
            }
            return;
        }

        // Cần ăn không?
        if (hunger > config.eatAt) return;

        // Tìm thức ăn trong hotbar (slot 0–8)
        int foodSlot = InventoryUtil.findFoodInHotbar(player);
        if (foodSlot == -1) {
            // Không có đồ ăn trong hotbar → tìm trong inventory và chuyển vào
            foodSlot = InventoryUtil.moveFoodToHotbar(player);
        }
        if (foodSlot == -1) return; // Không có đồ ăn

        // Cầm đồ ăn và bắt đầu ăn
        prevHeldSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = foodSlot;

        // Ăn mất ~32 tick (1.6 giây)
        eatTimer = 35;
        mc.options.useKey.setPressed(true);
    }
}
