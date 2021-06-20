package com.hamusuke.jece.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {

    }
}
