package com.oscarclient.core;

import com.oscarclient.gui.OscarScreen;
import com.oscarclient.modules.mining.SandMiner;
import com.oscarclient.modules.mining.Nuker;
import com.oscarclient.modules.survival.AutoEat;
import com.oscarclient.modules.survival.AutoRepair;
import com.oscarclient.modules.survival.AutoTotem;
import com.oscarclient.modules.safety.AntiLava;
import com.oscarclient.modules.safety.AntiPlayerAlert;
import com.oscarclient.modules.autobot.AutoRespawn;
import com.oscarclient.modules.autobot.AntiAfk;
import com.oscarclient.modules.autobot.AutoReconnect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Oscar Client — Điểm khởi đầu chính của mod (Client-side)
 * Đây là class đầu tiên được Fabric gọi khi mod load.
 */
public class OscarClient implements ClientModInitializer {

    // Logger để in thông báo ra console khi chạy
    public static final Logger LOGGER = LoggerFactory.getLogger("OscarClient");
    public static final String MOD_ID  = "oscarclient";
    public static final String VERSION = "1.0.0";

    // Singleton — chỉ có 1 instance duy nhất của mod
    private static OscarClient INSTANCE;
    public static OscarClient getInstance() { return INSTANCE; }

    // ── PHÍM TẮT ──
    // INSERT  → mở/đóng GUI Oscar Client
    // HOME    → bật/tắt toàn bộ mod nhanh
    private static KeyBinding KEY_OPEN_GUI;
    private static KeyBinding KEY_TOGGLE;

    // ── CONFIG ──
    public final OscarConfig config = new OscarConfig();

    // ── MODULE ── (mỗi tính năng = 1 module)
    public SandMiner      sandMiner;
    public Nuker          nuker;
    public AutoEat        autoEat;
    public AutoRepair     autoRepair;
    public AutoTotem      autoTotem;
    public AntiLava       antiLava;
    public AntiPlayerAlert antiPlayerAlert;
    public AutoRespawn    autoRespawn;
    public AntiAfk        antiAfk;
    public AutoReconnect  autoReconnect;

    // Trạng thái tổng: mod đang bật hay tắt
    private boolean running = false;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        LOGGER.info("[OscarClient] v{} đang khởi động...", VERSION);

        // Khởi tạo config (load từ file .json đã lưu)
        config.load();

        // Khởi tạo tất cả modules
        initModules();

        // Đăng ký phím tắt
        registerKeybinds();

        // Đăng ký tick event — chạy mỗi game tick (~50ms)
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

        LOGGER.info("[OscarClient] Khởi động xong! Nhấn INSERT để mở GUI.");
    }

    /** Khởi tạo tất cả modules */
    private void initModules() {
        sandMiner       = new SandMiner(config);
        nuker           = new Nuker(config);
        autoEat         = new AutoEat(config);
        autoRepair      = new AutoRepair(config);
        autoTotem       = new AutoTotem(config);
        antiLava        = new AntiLava(config);
        antiPlayerAlert = new AntiPlayerAlert(config);
        autoRespawn     = new AutoRespawn(config);
        antiAfk         = new AntiAfk(config);
        autoReconnect   = new AutoReconnect(config);
    }

    /** Đăng ký các phím tắt */
    private void registerKeybinds() {
        KEY_OPEN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.oscarclient.open_gui",            // tên key (dùng trong lang file)
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_INSERT,                  // phím INSERT
            "category.oscarclient"
        ));
        KEY_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.oscarclient.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_HOME,                    // phím HOME
            "category.oscarclient"
        ));
    }

    /**
     * Tick chính — chạy mỗi ~50ms (20 lần/giây).
     * Đây là nơi tất cả logic mod được gọi.
     */
    private void onTick(MinecraftClient mc) {
        // Kiểm tra phím mở GUI
        if (KEY_OPEN_GUI.wasPressed()) {
            if (mc.currentScreen == null) {
                mc.setScreen(new OscarScreen());  // Mở GUI
            } else if (mc.currentScreen instanceof OscarScreen) {
                mc.setScreen(null);               // Đóng GUI
            }
        }

        // Kiểm tra phím bật/tắt nhanh
        if (KEY_TOGGLE.wasPressed()) {
            toggleRunning();
        }

        // Nếu không trong game (đang ở menu, loading...) thì dừng
        if (mc.player == null || mc.world == null) return;

        // Chạy các module nếu mod đang bật
        if (running) {
            // ── An toàn trước ── (chạy trước kể cả module khác đang làm gì)
            if (config.antiLava)   antiLava.tick(mc);
            if (config.antiPlayer) antiPlayerAlert.tick(mc);

            // ── Sinh tồn ──
            if (config.autoEat)    autoEat.tick(mc);
            if (config.autoTotem)  autoTotem.tick(mc);
            if (config.autoRepair) autoRepair.tick(mc);

            // ── Đào cát ──
            if (config.nuker)      nuker.tick(mc);
            else if (config.mining) sandMiner.tick(mc);

            // ── Auto bot ──
            if (config.antiAfk)     antiAfk.tick(mc);
            if (config.autoRespawn) autoRespawn.tick(mc);
        }

        // Auto reconnect chạy kể cả khi tắt (để tự vào lại server)
        if (config.autoKick) autoReconnect.tick(mc);
    }

    /** Bật/tắt toàn bộ mod */
    public void toggleRunning() {
        running = !running;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.sendMessage(
                net.minecraft.text.Text.literal(
                    running ? "§a[Oscar] ✔ Đã BẬT" : "§c[Oscar] ✘ Đã TẮT"
                ), true  // true = hiện ở action bar (trên thanh máu), không chat
            );
        }
        LOGGER.info("[OscarClient] running = {}", running);
    }

    public boolean isRunning() { return running; }
    public void    setRunning(boolean v) { running = v; }
}
