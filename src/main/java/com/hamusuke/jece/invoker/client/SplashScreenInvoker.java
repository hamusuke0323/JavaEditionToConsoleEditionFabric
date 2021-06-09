package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceReloadMonitor;

import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface SplashScreenInvoker {
    ResourceReloadMonitor getResourceReloadMonitor();

    Consumer<Optional<Throwable>> getExceptionHandler();
}
