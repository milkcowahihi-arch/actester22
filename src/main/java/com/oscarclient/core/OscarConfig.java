package com.oscarclient.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.*;

/**
 * OscarConfig — Lưu/tải toàn bộ cài đặt của mod.
 * File config được lưu tại: .minecraft/config/oscar-client.json
 *
 * Gson tự động chuyển class này thành JSON và ngược lại.
 */
public class OscarConfig {

    // ═══════════════════════════════════════════
    // ĐÀO CÁT
    // ═══════════════════════════════════════════

    /** Bật tự đào cát bằng Baritone */
    public boolean mining      = true;

    /**
     * Chế độ đào:
     *   0 = Tự đào (Baritone)
     *   1 = Giữ phím W
     *   2 = Chỉ Nuker
     *   3 = Hybrid (Baritone + Nuker)
     */
    public int miningMode      = 0;

    /** Bật Nuker (phá block AOE xung quanh) */
    public boolean nuker       = true;

    /** Bán kính Nuker tính bằng block (1–8) */
    public int nukerRadius     = 4;

    /** Tốc độ Nuker (10–100%) */
    public int nukerSpeed      = 100;

    /** Tự giữ phím W đi tới */
    public boolean holdW       = true;

    /**
     * Phạm vi nhặt cát rơi:
     *   0 = Gần 4ô, 1 = Vừa 8ô, 2 = Xa 16ô, 3 = Siêu xa 24ô
     */
    public int pickupRange     = 2;

    /** Tự giao cát vào /orders khi đầy túi */
    public boolean autoDeliver = true;

    /** Giao thông minh — chờ đủ stack mới giao */
    public boolean smartDeliver = true;

    /** Tự khởi động lại khi Baritone/Nuker dừng */
    public boolean autoResume  = true;

    /** Tốc độ đào Baritone (10–100%) */
    public int miningSpeed     = 100;

    /** Anti detect — thêm delay ngẫu nhiên tránh anti-cheat */
    public boolean antiDetect  = true;

    /** Chống kẹt khối */
    public boolean antiStuck   = true;

    /** Smart Pathfinding */
    public boolean smartPath   = true;

    /** Đào nhiều tầng song song */
    public boolean multiLayer  = false;

    /** Số tầng đào (1–6) */
    public int layerDepth      = 3;

    /** Phạm vi scan tìm cát (4–64 block) */
    public int sandSearchRange = 16;

    // ═══════════════════════════════════════════
    // SINH TỒN
    // ═══════════════════════════════════════════

    /** Tự ăn khi đói */
    public boolean autoEat     = true;

    /** Ngưỡng đói để ăn (1–20) */
    public int eatAt           = 14;

    /** Tự mua đồ ăn tại /shop khi hết */
    public boolean buyFood     = true;

    /** Tự sửa xẻng */
    public boolean autoRepair  = true;

    /** Sửa khi xẻng còn dưới bao nhiêu % */
    public int repairAt        = 10;

    /** Tự mua bình XP */
    public boolean buyXp       = true;

    /** Vứt đồ rác (đất, đá, cuội...) */
    public boolean dropJunk    = true;

    /** Tự cầm Totem ở tay phụ */
    public boolean autoTotem   = true;

    /** Tự uống nước hồi máu (Regen/InstantHealth) */
    public boolean healPotion  = true;

    /** Uống nước hồi khi máu ≤ bao nhiêu tim */
    public int healAt          = 6;

    /** Kiểm tra giáp bị hỏng */
    public boolean armorCheck  = true;

    // ═══════════════════════════════════════════
    // AN TOÀN
    // ═══════════════════════════════════════════

    /** Thoát khỏi nước sâu / lava */
    public boolean antiDrown   = true;

    /** Bắc cầu cát qua nước */
    public boolean bridge      = true;

    /** Né hố và leo lên */
    public boolean antiPit     = true;

    /** Anti Lava — bịt nguồn lava phía trước */
    public boolean antiLava    = true;

    /** Anti TNT — chạy ra ngoài bán kính nổ */
    public boolean antiTnt     = true;

    /** Phát hiện player lạ gần mình */
    public boolean antiPlayer  = true;

    /** Bán kính cảnh báo player (block) */
    public int playerAlertRadius = 20;

    /** Thoát khẩn cấp /logout khi máu < 3 tim */
    public boolean emergencyLogout = false;

    // ═══════════════════════════════════════════
    // TỰ ĐÀO (AUTO BOT)
    // ═══════════════════════════════════════════

    /** Sau khi chết: hồi sinh → về chỗ chết nhặt đồ */
    public boolean recoverDeath = true;

    /** Tự bấm Hồi sinh khi chết */
    public boolean autoRespawn  = true;

    /** Đào xuyên cát chắn đường */
    public boolean tunnelSand   = true;

    /** Tự vào lại KingSMP khi bị kick */
    public boolean autoKick     = true;

    /** Dùng ngọc Ender để teleport về chỗ chết */
    public boolean enderPearl   = true;

    /** Anti AFK (tự di chuyển mỗi 25 giây) */
    public boolean antiAfk      = true;

    /** Khoảng thời gian Anti AFK (giây) */
    public int antiAfkInterval  = 25;

    /** Tự craft đồ khi thiếu */
    public boolean autoCraft    = false;

    /** Chạy lệnh tùy chỉnh khi bật mod */
    public boolean cmdOnStart   = false;

    /** Lệnh tùy chỉnh (vd: /home) */
    public String startCommand  = "";

    /** Lịch trình đào (bật/tắt theo giờ) */
    public boolean schedule     = false;

    /** Giờ bật (0–23) */
    public int scheduleOnHour   = 20;

    /** Giờ tắt (0–23) */
    public int scheduleOffHour  = 6;

    // ═══════════════════════════════════════════
    // THỐNG KÊ
    // ═══════════════════════════════════════════

    /** Giá ước tính mỗi cát (dùng tính tiền kiếm được) */
    public double pricePerSand  = 5.0;

    // ═══════════════════════════════════════════
    // LOAD / SAVE
    // ═══════════════════════════════════════════

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("oscar-client.json");

    /** Tải config từ file JSON. Nếu chưa có file thì dùng giá trị mặc định */
    public void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                OscarConfig loaded = GSON.fromJson(json, OscarConfig.class);
                // Copy tất cả fields từ loaded vào this
                copyFrom(loaded);
                OscarClient.LOGGER.info("[OscarClient] Config đã tải từ {}", CONFIG_PATH);
            } else {
                save(); // Tạo file mặc định nếu chưa có
            }
        } catch (Exception e) {
            OscarClient.LOGGER.error("[OscarClient] Lỗi tải config: {}", e.getMessage());
        }
    }

    /** Lưu config ra file JSON */
    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (Exception e) {
            OscarClient.LOGGER.error("[OscarClient] Lỗi lưu config: {}", e.getMessage());
        }
    }

    private void copyFrom(OscarConfig src) {
        if (src == null) return;
        this.mining         = src.mining;
        this.miningMode     = src.miningMode;
        this.nuker          = src.nuker;
        this.nukerRadius    = src.nukerRadius;
        this.nukerSpeed     = src.nukerSpeed;
        this.holdW          = src.holdW;
        this.pickupRange    = src.pickupRange;
        this.autoDeliver    = src.autoDeliver;
        this.smartDeliver   = src.smartDeliver;
        this.autoResume     = src.autoResume;
        this.miningSpeed    = src.miningSpeed;
        this.antiDetect     = src.antiDetect;
        this.antiStuck      = src.antiStuck;
        this.smartPath      = src.smartPath;
        this.multiLayer     = src.multiLayer;
        this.layerDepth     = src.layerDepth;
        this.sandSearchRange= src.sandSearchRange;
        this.autoEat        = src.autoEat;
        this.eatAt          = src.eatAt;
        this.buyFood        = src.buyFood;
        this.autoRepair     = src.autoRepair;
        this.repairAt       = src.repairAt;
        this.buyXp          = src.buyXp;
        this.dropJunk       = src.dropJunk;
        this.autoTotem      = src.autoTotem;
        this.healPotion     = src.healPotion;
        this.healAt         = src.healAt;
        this.armorCheck     = src.armorCheck;
        this.antiDrown      = src.antiDrown;
        this.bridge         = src.bridge;
        this.antiPit        = src.antiPit;
        this.antiLava       = src.antiLava;
        this.antiTnt        = src.antiTnt;
        this.antiPlayer     = src.antiPlayer;
        this.playerAlertRadius = src.playerAlertRadius;
        this.emergencyLogout= src.emergencyLogout;
        this.recoverDeath   = src.recoverDeath;
        this.autoRespawn    = src.autoRespawn;
        this.tunnelSand     = src.tunnelSand;
        this.autoKick       = src.autoKick;
        this.enderPearl     = src.enderPearl;
        this.antiAfk        = src.antiAfk;
        this.antiAfkInterval= src.antiAfkInterval;
        this.autoCraft      = src.autoCraft;
        this.cmdOnStart     = src.cmdOnStart;
        this.startCommand   = src.startCommand;
        this.schedule       = src.schedule;
        this.scheduleOnHour = src.scheduleOnHour;
        this.scheduleOffHour= src.scheduleOffHour;
        this.pricePerSand   = src.pricePerSand;
    }
}
