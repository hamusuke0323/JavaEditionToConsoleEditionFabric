package com.hamusuke.jece.client.invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public interface CreditsScreenInvoker {
    CreditsScreen setParentScreen(Screen parent);
}
