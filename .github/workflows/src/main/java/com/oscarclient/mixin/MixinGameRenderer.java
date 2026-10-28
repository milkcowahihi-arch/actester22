package com.oscarclient.mixin;

import com.oscarclient.core.OscarClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinGameRenderer — Hook vào vòng render chính.
 * Dùng để vẽ HUD overlay của Oscar Client lên màn hình.
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    /**
     * Hook vào cuối mỗi frame render.
     * Tại đây ta có thể vẽ text, box, overlay lên màn hình.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(net.minecraft.client.util.math.MatrixStack matrices,
                          float tickDelta, long startTime, boolean tick,
                          CallbackInfo ci) {
        // HUD rendering được xử lý trong MixinInGameHud
        // GameRenderer hook này dự phòng cho future features
    }
}
