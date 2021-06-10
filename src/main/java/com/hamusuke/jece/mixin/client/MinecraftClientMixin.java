package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.gui.screen.ProgressBarScreen;
import com.hamusuke.jece.client.gui.screen.StartupScreen;
import com.hamusuke.jece.invoker.client.MinecraftClientInvoker;
import com.hamusuke.jece.invoker.client.SplashScreenInvoker;
import com.hamusuke.jece.client.util.CEUtil;
import com.hamusuke.jece.client.util.StartupSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

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

    @Shadow
    public abstract void disconnect(Screen screen);

    @Shadow
    @Final
    private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;
    private RotatingCubeMapRenderer panorama;
    private StartupSoundPlayer startupSoundPlayer;
    private boolean isCreateWorld;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void MinecraftClient(RunArgs args, CallbackInfo ci) {
        if (JECEClient.isFirst && this.overlay instanceof SplashScreen) {
            SplashScreenInvoker invoker = (SplashScreenInvoker) this.overlay;
            StartupScreen.loadStartupTextures((MinecraftClient) (Object) this);
            this.setOverlay(null);
            InputStream inputStream = DefaultResourcePack.class.getResourceAsStream("/assets/" + JECE.MOD_ID + "/sounds/gamestart.ogg");
            if (inputStream != null) {
                try {
                    this.startupSoundPlayer = new StartupSoundPlayer(inputStream);
                    this.startupSoundPlayer.play();
                } catch (IOException e) {
                    LOGGER.warn("IOException occurred in StartupSoundPlayer", e);
                }
            } else {
                LOGGER.warn("StartupSound not found, return null!");
            }
            this.openScreen(new StartupScreen((MinecraftClient) (Object) this, invoker.getResourceReloadMonitor(), invoker.getExceptionHandler()));
        }
    }

    @ModifyArg(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), index = 0)
    private Screen startIntegratedServerI(Screen screenIn) {
        if (screenIn instanceof LevelLoadingScreen) {
            ProgressBarScreen progressBarScreen;
            if (this.isCreateWorld) {
                progressBarScreen = new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("creating.spawn.area"), this.worldGenProgressTracker.get(), false);
            } else {
                progressBarScreen = new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("loading.spawn.area"), this.worldGenProgressTracker.get(), false);
            }
            return progressBarScreen;
        }

        return screenIn;
    }

    @Inject(method = "disconnect()V", at = @At("HEAD"), cancellable = true)
    private void disconnect(CallbackInfo ci) {
        if (this.isCreateWorld) {
            this.disconnect(new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("finding.seed")));
        } else {
            this.disconnect(new ProgressBarScreen());
        }

        ci.cancel();
    }

    @Inject(method = "method_29607", at = @At("HEAD"))
    private void method_29607H(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        this.isCreateWorld = true;
    }

    @Inject(method = "method_29607", at = @At("RETURN"))
    private void method_29607R(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        this.isCreateWorld = false;
    }

    public StartupSoundPlayer getPlayer() {
        return this.startupSoundPlayer;
    }

    public RotatingCubeMapRenderer getPanorama() {
        if (this.panorama == null) {
            this.panorama = new RotatingCubeMapRenderer(CEUtil.PANORAMA_RESOURCES_CE);
        }

        return this.panorama;
    }

    public boolean isCreateWorld() {
        return this.isCreateWorld;
    }
}
