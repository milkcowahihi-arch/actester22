package com.oscarclient.modules.mining;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.util.ChatUtil;
import com.oscarclient.util.InventoryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * SandMiner — Module tự đào cát chính.
 *
 * Cách hoạt động:
 *   1. Quét khu vực xung quanh tìm block Cát (SAND / RED_SAND)
 *   2. Tìm block gần nhất
 *   3. Dùng Baritone API pathfind đến đó và đào
 *   4. Sau khi đào xong → deliver lên /orders
 *
 * NOTE: Cần có Baritone trong classpath để dùng BaritoneAPI.
 *       Nếu chưa có Baritone, module sẽ dùng fallback (đi thẳng + đào thủ công).
 */
public class SandMiner {

    private final OscarConfig config;

    // Trạng thái nội bộ của module
    private enum State {
        IDLE,           // đang chờ
        FINDING_SAND,   // đang tìm cát
        MOVING_TO_SAND, // đang đi đến cát
        MINING_SAND,    // đang đào cát
        DELIVERING,     // đang giao cát (/orders)
        BUYING_SUPPLIES // đang mua đồ (/shop)
    }

    private State   state       = State.IDLE;
    private BlockPos targetBlock = null;
    private int     tickCount   = 0;      // đếm tick để delay
    private int     stuckTimer  = 0;      // đếm tick để phát hiện kẹt
    private Vec3d   lastPos     = null;   // vị trí tick trước (dùng phát hiện kẹt)
    private int     deliverWait = 0;      // đợi sau khi giao cát

    // Thống kê
    public int sandMined    = 0;
    public int sandDelivered= 0;
    public double moneyEarned = 0;

    public SandMiner(OscarConfig config) {
        this.config = config;
    }

    /**
     * Được gọi mỗi game tick (~50ms).
     * Đây là "bộ não" của module — quyết định phải làm gì tiếp theo.
     */
    public void tick(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        tickCount++;

        // Kiểm tra kẹt (nếu không di chuyển 60 tick = 3 giây mà đang di chuyển)
        if (config.antiStuck && state == State.MOVING_TO_SAND) {
            checkStuck(player);
        }

        // Nhặt cát rơi xung quanh (attract items)
        if (tickCount % 5 == 0) {
            pickupNearbySand(player);
        }

        switch (state) {

            case IDLE -> {
                // Kiểm tra đã đủ cát để giao chưa
                if (config.autoDeliver && shouldDeliver(player)) {
                    state = State.DELIVERING;
                    return;
                }
                // Tìm cát tiếp
                state = State.FINDING_SAND;
            }

            case FINDING_SAND -> {
                targetBlock = findNearestSand(mc, config.sandSearchRange);
                if (targetBlock != null) {
                    state = State.MOVING_TO_SAND;
                    stuckTimer = 0;
                    lastPos = player.getPos();
                    ChatUtil.actionBar(mc, "§e[Oscar] Tìm thấy cát tại " + targetBlock.toShortString());
                } else {
                    // Không tìm thấy cát trong vùng → đứng yên chờ
                    state = State.IDLE;
                }
            }

            case MOVING_TO_SAND -> {
                if (targetBlock == null) { state = State.IDLE; return; }

                // Nếu đã đến gần block (trong 2 block) → bắt đầu đào
                double distSq = player.getPos().squaredDistanceTo(
                    targetBlock.getX() + 0.5, player.getY(), targetBlock.getZ() + 0.5);
                if (distSq < 4.0) {
                    state = State.MINING_SAND;
                    return;
                }

                // Pathfind đến target
                // Nếu có Baritone: BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess()...
                // Nếu không có Baritone: dùng moveToward fallback
                moveToward(player, targetBlock);
            }

            case MINING_SAND -> {
                if (targetBlock == null || mc.world == null) { state = State.IDLE; return; }

                // Kiểm tra block vẫn còn là cát
                var blockState = mc.world.getBlockState(targetBlock);
                if (!isSandBlock(blockState.getBlock())) {
                    // Đã đào xong hoặc block không còn là cát
                    sandMined++;
                    state = State.IDLE;
                    return;
                }

                // Đào block: nhìn vào block và giữ nút đào
                lookAt(mc, targetBlock);
                mc.options.attackKey.setPressed(true);  // giữ nút tấn công = đào

                // Anti detect: thỉnh thoảng nghỉ ngắn
                if (config.antiDetect && tickCount % (18 + new Random().nextInt(7)) == 0) {
                    mc.options.attackKey.setPressed(false);
                }
            }

            case DELIVERING -> {
                deliverWait++;
                if (deliverWait == 1) {
                    // Tick đầu tiên: gõ lệnh /orders
                    ChatUtil.sendCommand(mc, "orders");
                } else if (deliverWait > 60) {
                    // Sau 3 giây: giả sử đã giao xong
                    sandDelivered += InventoryUtil.getSandCount(player);
                    moneyEarned = sandDelivered * config.pricePerSand;
                    deliverWait = 0;
                    state = State.IDLE;
                    ChatUtil.actionBar(mc, "§a[Oscar] Đã giao cát! Tổng: " + sandDelivered);
                }
            }

            case BUYING_SUPPLIES -> {
                // Mở /shop và mua đồ cần thiết
                if (deliverWait == 0) {
                    ChatUtil.sendCommand(mc, "shop");
                }
                deliverWait++;
                if (deliverWait > 80) {
                    deliverWait = 0;
                    state = State.IDLE;
                }
            }
        }
    }

    // ════════════════════════════════════════════
    //  HELPER METHODS
    // ════════════════════════════════════════════

    /**
     * Quét bán kính `range` block xung quanh player, trả về BlockPos cát gần nhất.
     */
    private BlockPos findNearestSand(MinecraftClient mc, int range) {
        if (mc.player == null || mc.world == null) return null;
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos nearest   = null;
        double   minDist   = Double.MAX_VALUE;

        for (int x = -range; x <= range; x++) {
            for (int y = -4; y <= 4; y++) {          // quét 4 block lên/xuống
                for (int z = -range; z <= range; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    var block = mc.world.getBlockState(pos).getBlock();
                    if (isSandBlock(block)) {
                        double dist = playerPos.getSquaredDistance(pos);
                        if (dist < minDist) {
                            minDist = dist;
                            nearest = pos;
                        }
                    }
                }
            }
        }
        return nearest;
    }

    /** Kiểm tra block có phải cát không */
    private boolean isSandBlock(net.minecraft.block.Block block) {
        return block == Blocks.SAND || block == Blocks.RED_SAND;
    }

    /** Nhặt item Entity cát xung quanh player */
    private void pickupNearbySand(ClientPlayerEntity player) {
        // Minecraft tự nhặt item khi đứng gần — method này chỉ để mở rộng sau
        // Có thể dùng để attract item bằng cách di chuyển về phía item entity
        int pickupBlocks = switch (config.pickupRange) {
            case 0 -> 4;
            case 1 -> 8;
            case 2 -> 16;
            case 3 -> 24;   // Siêu xa 24ô — mới!
            default -> 8;
        };
        // Logic attract items sẽ được implement thêm
    }

    /** Kiểm tra có nên giao cát không */
    private boolean shouldDeliver(ClientPlayerEntity player) {
        int sandCount = InventoryUtil.getSandCount(player);
        if (config.smartDeliver) {
            return sandCount >= 64; // Chờ đủ 1 stack (64) mới giao
        }
        return sandCount >= 32; // Giao khi có 32 cát
    }

    /** Di chuyển về phía target block (fallback khi không có Baritone) */
    private void moveToward(ClientPlayerEntity player, BlockPos target) {
        double dx = target.getX() + 0.5 - player.getX();
        double dz = target.getZ() + 0.5 - player.getZ();
        double yaw = Math.toDegrees(Math.atan2(-dx, dz));
        player.setYaw((float) yaw);

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.forwardKey.setPressed(true);
    }

    /** Nhìn về phía block */
    private void lookAt(MinecraftClient mc, BlockPos pos) {
        if (mc.player == null) return;
        double dx = pos.getX() + 0.5 - mc.player.getX();
        double dy = pos.getY() + 0.5 - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double dz = pos.getZ() + 0.5 - mc.player.getZ();
        double dist = Math.sqrt(dx*dx + dz*dz);
        float yaw   = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    /** Phát hiện kẹt: nếu không di chuyển 3 giây thì reset */
    private void checkStuck(ClientPlayerEntity player) {
        Vec3d pos = player.getPos();
        if (lastPos != null && pos.squaredDistanceTo(lastPos) < 0.01) {
            stuckTimer++;
            if (stuckTimer > 60) { // 60 tick = 3 giây
                // Kẹt! Reset target và tìm đường khác
                targetBlock = null;
                state = State.IDLE;
                stuckTimer = 0;
                ChatUtil.actionBar(MinecraftClient.getInstance(), "§c[Oscar] Bị kẹt! Đổi hướng...");
            }
        } else {
            stuckTimer = 0;
        }
        lastPos = pos;
    }

    /** Reset toàn bộ trạng thái module */
    public void reset() {
        state       = State.IDLE;
        targetBlock = null;
        tickCount   = 0;
        stuckTimer  = 0;
        deliverWait = 0;
    }

    public State getState() { return state; }
}
