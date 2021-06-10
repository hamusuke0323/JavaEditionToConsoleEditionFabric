package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.gui.screen.ProgressBarScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.Entry.class)
@Environment(EnvType.CLIENT)
public class WorldListWidgetEntryMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "method_29990", at = @At("HEAD"), cancellable = true)
    private void method_29990(CallbackInfo ci) {
        this.client.openScreen(new ProgressBarScreen());
        ci.cancel();
    }
}
