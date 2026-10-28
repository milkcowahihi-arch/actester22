package com.oscarclient.mixin;

import com.oscarclient.core.OscarClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinClientPlayerEntity — Hook vào ClientPlayerEntity.
 *
 * Dùng để:
 *   - Phát hiện player chết (onDeath)
 *   - Hook trước khi packet được gửi (tuỳ chỉnh movement)
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    /**
     * Hook khi player chết — ghi nhận tọa độ chết.
     * @reason Lưu vị trí để sau này về nhặt đồ
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(net.minecraft.entity.damage.DamageSource source, CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
        var pos = self.getPos();
        OscarClient.LOGGER.info(
            "[Oscar] Player chết tại X:{} Y:{} Z:{}",
            (int)pos.x, (int)pos.y, (int)pos.z
        );
        // Lưu vị trí chết để AutoRespawn module dùng
        // (sẽ implement đầy đủ ở version sau)
    }

    /**
     * Hook khi tick — để override movement khi mod đang bật.
     */
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        // Logic tick đã được xử lý trong OscarClient.onTick()
        // Mixin này chỉ là entry point dự phòng
    }
}
