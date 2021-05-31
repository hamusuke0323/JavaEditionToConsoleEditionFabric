package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.MainClient;
import com.hamusuke.jece.client.screen.StartupScreen;
import com.hamusuke.jece.client.invoker.MinecraftClientInvoker;
import com.hamusuke.jece.client.invoker.SplashScreenInvoker;
import com.hamusuke.jece.client.utils.StartupSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.resource.DefaultResourcePack;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin implements MinecraftClientInvoker {
    @Shadow
    @Nullable
    public Overlay overlay;

    @Shadow
    public abstract void openScreen(@Nullable Screen screen);

    @Shadow
    public abstract void setOverlay(@Nullable Overlay overlay);

    @Shadow
    @Final
    private static Logger LOGGER;

    private StartupSoundPlayer startupSoundPlayer;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void MinecraftClient(RunArgs args, CallbackInfo ci) {
        if (MainClient.isFirst && this.overlay instanceof SplashScreen) {
            SplashScreenInvoker invoker = (SplashScreenInvoker) this.overlay;
            StartupScreen.loadStartupTextures((MinecraftClient) (Object) this);

            InputStream is = DefaultResourcePack.class.getResourceAsStream("/assets/" + MainClient.MOD_ID + "/sound/gamestart.ogg");
            if (is != null) {
                try {
                    this.startupSoundPlayer = new StartupSoundPlayer(is);
                    this.startupSoundPlayer.play();
                } catch (IOException e) {
                    LOGGER.warn("IOException occurred in StartupSoundPlayer", e);
                }
            } else {
                LOGGER.warn("StartupSound not found, return null!");
            }

            this.openScreen(new StartupScreen((MinecraftClient) (Object) this, invoker.getResourceReloadMonitor(), invoker.getExceptionHandler()));
            this.setOverlay(null);
        }
    }

    public StartupSoundPlayer getPlayer() {
        return this.startupSoundPlayer;
    }
}
