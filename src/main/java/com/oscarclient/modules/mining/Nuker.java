package com.oscarclient.modules.mining;

import com.oscarclient.core.OscarConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Nuker — Tự động phá block cát xung quanh player theo vòng tròn AOE.
 *
 * Cách hoạt động:
 *   - Mỗi tick: quét tất cả block trong bán kính nukerRadius
 *   - Sắp xếp từ gần nhất → xa nhất
 *   - Gửi packet đào về server (KHÔNG click chuột — nhanh hơn và linh hoạt hơn)
 *   - Có anti-detect: thêm delay ngẫu nhiên giữa các block
 */
public class Nuker {

    private final OscarConfig config;
    private final Random rng = new Random();

    // Delay giữa các lần đào (tick)
    private int cooldown = 0;

    // Thống kê
    public int blocksDestroyed = 0;

    public Nuker(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        // Cooldown giữa các lần đào
        if (cooldown > 0) { cooldown--; return; }

        ClientPlayerEntity player = mc.player;
        List<BlockPos> targets = findSandInRadius(mc, config.nukerRadius);

        if (targets.isEmpty()) return;

        // Số block đào mỗi tick = tốc độ Nuker / 10 (tốc độ 100% = 10 block/tick)
        int maxPerTick = Math.max(1, config.nukerSpeed / 10);

        for (int i = 0; i < Math.min(maxPerTick, targets.size()); i++) {
            BlockPos pos = targets.get(i);

            // Gửi packet đào block về server
            // PlayerActionC2SPacket.Action.START_DESTROY_BLOCK = bắt đầu đào
            // PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK  = xong đào
            if (mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(
                    new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                        pos,
                        Direction.DOWN,
                        0
                    )
                );
                mc.getNetworkHandler().sendPacket(
                    new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                        pos,
                        Direction.DOWN,
                        0
                    )
                );
            }

            blocksDestroyed++;
        }

        // Anti detect: delay ngẫu nhiên
        if (config.antiDetect) {
            cooldown = 1 + rng.nextInt(3); // 1–3 tick delay
        } else {
            cooldown = 1;
        }
    }

    /**
     * Tìm tất cả block cát trong bán kính r quanh player.
     * Trả về list đã sắp xếp từ gần → xa.
     */
    private List<BlockPos> findSandInRadius(MinecraftClient mc, int r) {
        List<BlockPos> result = new ArrayList<>();
        if (mc.player == null || mc.world == null) return result;

        BlockPos center = mc.player.getBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    // Chỉ lấy block trong hình CẦU (không phải cube)
                    if (x*x + y*y + z*z > r*r) continue;

                    BlockPos pos = center.add(x, y, z);
                    var block = mc.world.getBlockState(pos).getBlock();

                    if (block == Blocks.SAND || block == Blocks.RED_SAND) {
                        result.add(pos);
                    }
                }
            }
        }

        // Sắp xếp gần → xa để đào block gần nhất trước
        result.sort(Comparator.comparingDouble(pos -> center.getSquaredDistance(pos)));
        return result;
    }
}
