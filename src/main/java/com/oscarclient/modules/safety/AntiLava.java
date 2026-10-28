package com.oscarclient.modules.safety;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.core.OscarClient;
import com.oscarclient.util.ChatUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * AntiLava — Phát hiện lava phía trước và xử lý.
 *
 * Các trường hợp:
 *   1. Lava ngay dưới chân → dừng đào, cố lên
 *   2. Lava phía trước (trong 3 block) → dừng đào, bịt bằng cát
 *   3. Player đang trong lava → tự bơi lên
 */
public class AntiLava {

    private final OscarConfig config;
    private boolean inLava = false;

    public AntiLava(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null) return;

        // Kiểm tra player đang trong lava
        boolean currentlyInLava = player.isInLava();

        if (currentlyInLava && !inLava) {
            inLava = true;
            // Dừng toàn bộ mod tạm thời
            ChatUtil.actionBar(mc, "§c[Oscar] ⚠ Trong LAVA! Đang thoát...");
            mc.options.jumpKey.setPressed(true); // Nhảy lên
            mc.options.attackKey.setPressed(false);
        } else if (!currentlyInLava && inLava) {
            inLava = false;
            mc.options.jumpKey.setPressed(false);
        }

        if (currentlyInLava) {
            // Nhảy liên tục để thoát lava
            mc.options.jumpKey.setPressed(true);
            return;
        }

        // Quét lava phía trước trong 3 block
        BlockPos playerPos = player.getBlockPos();
        Direction facing = player.getHorizontalFacing();

        for (int i = 1; i <= 3; i++) {
            BlockPos check = playerPos.offset(facing, i);
            var blockBelow = mc.world.getBlockState(check.down()).getBlock();
            var blockAt    = mc.world.getBlockState(check).getBlock();
            var blockAbove = mc.world.getBlockState(check.up()).getBlock();

            boolean lavaNearby = blockBelow == Blocks.LAVA || blockAt == Blocks.LAVA || blockAbove == Blocks.LAVA;

            if (lavaNearby) {
                // Dừng di chuyển
                mc.options.forwardKey.setPressed(false);
                mc.options.attackKey.setPressed(false);

                // Dừng toàn bộ mod
                OscarClient.getInstance().setRunning(false);
                ChatUtil.actionBar(mc, "§c[Oscar] ⚠ Lava phía trước! Đã DỪNG.");
                break;
            }
        }
    }
}
