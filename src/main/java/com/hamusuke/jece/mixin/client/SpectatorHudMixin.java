package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.util.CEUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectatorHud.class)
@Environment(EnvType.CLIENT)
public class SpectatorHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At("HEAD"), cancellable = true)
    private void render(MatrixStack matrixStack, CallbackInfo ci) {
        if (CEUtil.cantRenderHotbars(this.client)) {
            ci.cancel();
        }
    }
}
