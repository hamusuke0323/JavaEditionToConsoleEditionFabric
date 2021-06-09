package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;

@Environment(EnvType.CLIENT)
public interface AdvancementsScreenInvoker {
    AdvancementsScreen setParentScreen(Screen parent);
}
