package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.invoker.client.SplashScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashScreen.class)
@Environment(EnvType.CLIENT)
public class SplashScreenMixin implements SplashScreenInvoker {
    @Shadow
    @Final
    private Consumer<Optional<Throwable>> exceptionHandler;

    @Shadow
    @Final
    private ResourceReload reload;

    public ResourceReload getResourceReloadMonitor() {
        return this.reload;
    }

    public Consumer<Optional<Throwable>> getExceptionHandler() {
        return this.exceptionHandler;
    }
}
