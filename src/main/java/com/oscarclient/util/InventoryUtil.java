package com.oscarclient.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;

public class InventoryUtil {

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

    public static int findFoodInHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            var stack = inv.getStack(i);
            if (stack.contains(DataComponentTypes.FOOD)) return i;
        }
        return -1;
    }

    public static int moveFoodToHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 9; i < 36; i++) {
            var stack = inv.getStack(i);
            if (stack.contains(DataComponentTypes.FOOD)) {
                var tmp = inv.getStack(8).copy();
                inv.setStack(8, stack.copy());
                inv.setStack(i, tmp);
                return 8;
            }
        }
        return -1;
    }

    public static int findShovelInHotbar(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).getItem() instanceof ShovelItem) return i;
        }
        return -1;
    }

    public static boolean isInventoryFull(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    public static int findTotem(ClientPlayerEntity player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }
}
