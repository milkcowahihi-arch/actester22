# ⬡ Oscar Client
> Mod Fabric tự động đào cát cho server **KingSMP** — Minecraft 1.21.1

---

## 📋 Tính năng

| Module | Tính năng |
|--------|-----------|
| **Đào cát** | Tự đào · Nuker AOE · Giữ phím W · Hybrid · Siêu xa 24ô |
| **Sinh tồn** | Tự ăn · Sửa xẻng · Mua đồ · Cầm Totem · Hồi máu |
| **An toàn** | Anti Lava · Anti TNT · Phát hiện player · Thoát khẩn cấp |
| **Auto Bot** | Tự hồi sinh · Nhặt đồ · Anti AFK · Tự vào KingSMP |
| **GUI** | Phím INSERT mở giao diện · HUD overlay góc trên phải |

---

## 🛠️ Yêu cầu để build

| Thứ cần | Phiên bản |
|---------|-----------|
| Java JDK | **21** (bắt buộc) |
| IntelliJ IDEA | Community (miễn phí) hoặc Ultimate |
| Git | Bất kỳ |

---

## 🚀 Cách build từ đầu (step-by-step)

### Bước 1 — Cài Java 21
- Tải tại: https://adoptium.net/temurin/releases/?version=21
- Chọn **Windows x64 → .msi** → cài xong

### Bước 2 — Cài IntelliJ IDEA
- Tải tại: https://www.jetbrains.com/idea/download (bản Community miễn phí)

### Bước 3 — Clone project về máy
```bash
git clone https://github.com/TÊN_BẠN/oscar-client.git
cd oscar-client
```

### Bước 4 — Mở IntelliJ
1. Mở IntelliJ → **Open** → chọn thư mục `oscar-client`
2. IntelliJ sẽ tự nhận ra đây là Gradle project
3. Chờ Gradle sync xong (có thể 3–5 phút lần đầu)

### Bước 5 — (Tùy chọn) Thêm Baritone
Baritone giúp bot tự tìm đường đến cát thông minh hơn.
1. Tải: https://github.com/cabaletta/baritone/releases
2. Chọn file `baritone-unoptimized-fabric-1.21.1-*.jar`
3. Tạo thư mục `libs/` trong project
4. Đặt file .jar vào đó
5. Bỏ comment dòng `implementation fileTree(...)` trong `build.gradle`
6. Gradle sync lại

### Bước 6 — Chạy Minecraft với mod (để test)
Trong IntelliJ, mở tab **Gradle** (bên phải) →
`oscar-client → Tasks → fabric → runClient` → double click

### Bước 7 — Build file .jar để dùng
```bash
# Trong terminal của IntelliJ (View → Tool Windows → Terminal)
./gradlew build
```
File .jar xuất hiện tại: `build/libs/oscar-client-1.0.0.jar`

### Bước 8 — Cài vào Minecraft
1. Cài **Fabric Loader 1.21.1**: https://fabricmc.net/use/installer/
2. Cài **Fabric API**: https://modrinth.com/mod/fabric-api
3. Copy `oscar-client-1.0.0.jar` vào thư mục `.minecraft/mods/`
4. Mở Minecraft với profile **Fabric 1.21.1**

---

## ⌨️ Phím tắt trong game

| Phím | Chức năng |
|------|-----------|
| `INSERT` | Mở / Đóng GUI Oscar Client |
| `HOME` | Bật / Tắt nhanh toàn bộ mod |
| `ESC` | Đóng GUI (khi đang mở) |

---

## 📁 Cấu trúc thư mục

```
oscar-client/
├── src/main/java/com/oscarclient/
│   ├── core/
│   │   ├── OscarClient.java      ← Điểm khởi đầu chính
│   │   └── OscarConfig.java      ← Tất cả cài đặt (lưu/tải JSON)
│   ├── modules/
│   │   ├── mining/
│   │   │   ├── SandMiner.java    ← Logic tự đào cát
│   │   │   └── Nuker.java        ← Nuker AOE
│   │   ├── survival/
│   │   │   ├── AutoEat.java      ← Tự ăn
│   │   │   ├── AutoRepair.java   ← Tự sửa xẻng
│   │   │   └── AutoTotem.java    ← Cầm Totem
│   │   ├── safety/
│   │   │   ├── AntiLava.java     ← Chống lava
│   │   │   └── AntiPlayerAlert.java ← Phát hiện player
│   │   └── autobot/
│   │       ├── AutoRespawn.java  ← Tự hồi sinh
│   │       ├── AntiAfk.java      ← Chống AFK
│   │       └── AutoReconnect.java ← Tự vào lại KingSMP
│   ├── gui/
│   │   └── OscarScreen.java     ← GUI trong game (INSERT)
│   ├── mixin/
│   │   ├── MixinGameRenderer.java
│   │   ├── MixinClientPlayerEntity.java
│   │   └── MixinInGameHud.java  ← HUD overlay
│   └── util/
│       ├── ChatUtil.java         ← Gửi lệnh/tin nhắn
│       └── InventoryUtil.java    ← Thao tác inventory
├── build.gradle
├── gradle.properties            ← Đổi version Minecraft tại đây
└── README.md
```

---

## ⚙️ File config

Config tự lưu tại: `.minecraft/config/oscar-client.json`
Mở bằng Notepad để sửa thủ công nếu cần.

---

## ❓ Lỗi thường gặp

| Lỗi | Cách sửa |
|-----|----------|
| `Java 21 not found` | Cài JDK 21 từ adoptium.net |
| `Could not resolve fabric-api` | Kiểm tra kết nối internet, chạy `./gradlew build --refresh-dependencies` |
| Mod không hiện trong game | Kiểm tra đã cài Fabric Loader + Fabric API chưa |
| `Mixin injection failed` | Kiểm tra file `oscarclient.mixins.json` khớp với tên class |

---

## 📤 Upload lên GitHub

```bash
git init
git add .
git commit -m "Oscar Client v1.0.0"
git branch -M main
git remote add origin https://github.com/TÊN_BẠN/oscar-client.git
git push -u origin main
```

Sau mỗi lần sửa code:
```bash
git add .
git commit -m "Mô tả thay đổi"
git push
```

GitHub Actions sẽ tự động build và tạo file .jar.

---

## ⚠️ Lưu ý

- Mod này chỉ dùng cho mục đích học tập và cá nhân.
- Sử dụng trên server cần được phép của chủ server.
- Luôn backup file config trước khi cập nhật.
