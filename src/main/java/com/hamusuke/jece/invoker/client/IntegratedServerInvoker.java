package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IntegratedServerInvoker {
    void saveAll();
}