package com.oscarclient.modules.survival;

import com.oscarclient.core.OscarConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

/**
 * AutoTotem — Luôn giữ Totem of Undying ở tay phụ (offhand).
 *
 * Tay phụ trong Minecraft = slot 45 trong inventory.
 * Mỗi tick kiểm tra: nếu offhand không có Totem → chuyển Totem từ inventory vào.
 */
public class AutoTotem {

    private final OscarConfig config;

    public AutoTotem(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) return;

        // Kiểm tra tay phụ có Totem chưa
        var offhand = player.getOffHandStack();
        if (offhand.getItem() == Items.TOTEM_OF_UNDYING) return; // Đã có rồi

        // Tìm Totem trong inventory (slot 0–35)
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                // Swap slot này với offhand (slot 45)
                // syncId = 0 (player inventory), slot i, button 0, action PICKUP
                mc.interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,
                    i,       // slot nguồn
                    0,
                    SlotActionType.PICKUP,
                    player
                );
                // Click vào offhand slot (slot 45)
                mc.interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,
                    45,      // offhand slot
                    0,
                    SlotActionType.PICKUP,
                    player
                );
                // Click lại slot nguồn để đặt item cũ về
                mc.interactionManager.clickSlot(
                    player.playerScreenHandler.syncId,
                    i,
                    0,
                    SlotActionType.PICKUP,
                    player
                );
                break;
            }
        }
    }
}
