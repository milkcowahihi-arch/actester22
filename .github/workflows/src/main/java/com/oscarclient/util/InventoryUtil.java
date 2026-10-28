package com.oscarclient.util;

import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;

/**
 * InventoryUtil — Tiện ích thao tác với inventory.
 */
public class InventoryUtil {

    /**
     * Đếm tổng số cát trong toàn bộ inventory
     */
    public static int getSandCount(ClientPlayerEntity player) {
        int count = 0;
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            var stack = inv.getStack(i);
            if (stack.getItem() == Items.SAND || stack.getItem() == Items.RED_SAND) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /**
     * Tìm thức ăn trong hotbar (slot 0–8).
     * @return slot index hoặc -1 nếu không tìm thấy
     */
    public static int findFoodInHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            var stack = inv.getStack(i);
            if (stack.getItem().isFood()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Tìm thức ăn trong inventory chính (slot 9–35) và chuyển vào hotbar slot 8.
     * @return slot hotbar đã chuyển vào hoặc -1
     */
    public static int moveFoodToHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 9; i < 36; i++) {
            var stack = inv.getStack(i);
            if (stack.getItem().isFood()) {
                // Swap với slot 8 (hotbar cuối)
                var tmp = inv.getStack(8).copy();
                inv.setStack(8, stack.copy());
                inv.setStack(i, tmp);
                return 8;
            }
        }
        return -1;
    }

    /**
     * Kiểm tra xẻng trong hotbar
     * @return slot index xẻng hoặc -1
     */
    public static int findShovelInHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).getItem() instanceof ShovelItem) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Kiểm tra inventory có đầy không (slot 9–35)
     */
    public static boolean isInventoryFull(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    /**
     * Tìm Totem trong inventory
     * @return slot index hoặc -1
     */
    public static int findTotem(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }
}
