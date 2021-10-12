package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceReload;

import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface SplashScreenInvoker {
    ResourceReload getResourceReloadMonitor();

    Consumer<Optional<Throwable>> getExceptionHandler();
}
