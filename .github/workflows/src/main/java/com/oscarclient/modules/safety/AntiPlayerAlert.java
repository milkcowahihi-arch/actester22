package com.oscarclient.modules.safety;

import com.oscarclient.core.OscarConfig;
import com.oscarclient.core.OscarClient;
import com.oscarclient.util.ChatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * AntiPlayerAlert — Phát hiện player lạ gần mình và cảnh báo / tạm dừng.
 *
 * Cách hoạt động:
 *   - Mỗi 40 tick (2 giây): quét entity player trong bán kính config.playerAlertRadius
 *   - Loại bỏ chính bản thân và player trong friend list (chưa implement)
 *   - Nếu có player lạ → tạm dừng mod, thông báo
 */
public class AntiPlayerAlert {

    private final OscarConfig config;
    private int scanTimer = 0;
    private boolean paused = false;  // mod có đang bị pause vì player không

    public AntiPlayerAlert(OscarConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient mc) {
        if (++scanTimer < 40) return; // scan mỗi 2 giây
        scanTimer = 0;

        ClientPlayerEntity self = mc.player;
        if (self == null || mc.world == null) return;

        // Lấy tất cả player entities gần mình
        List<PlayerEntity> nearby = mc.world.getEntitiesByClass(
            PlayerEntity.class,
            self.getBoundingBox().expand(config.playerAlertRadius),
            p -> !p.equals(self)   // loại bản thân
        );

        if (!nearby.isEmpty()) {
            String names = nearby.stream()
                .map(p -> p.getName().getString())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

            if (!paused) {
                paused = true;
                ChatUtil.actionBar(mc, "§e[Oscar] ⚠ Player lạ gần: §f" + names + " §e— Tạm dừng!");
                // Tạm dừng module đào (không tắt hẳn)
                OscarClient.getInstance().sandMiner.reset();
                mc.options.forwardKey.setPressed(false);
                mc.options.attackKey.setPressed(false);
            }
        } else {
            if (paused) {
                paused = false;
                ChatUtil.actionBar(mc, "§a[Oscar] Player đã đi — Tiếp tục đào.");
            }
        }
    }

    public boolean isPaused() { return paused; }
}
