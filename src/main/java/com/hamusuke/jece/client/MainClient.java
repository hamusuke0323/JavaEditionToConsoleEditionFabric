package com.hamusuke.jece.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
    public static final String MOD_ID = "jece";
    public static boolean isFirst = true;

    public void onInitializeClient() {

    }
}
