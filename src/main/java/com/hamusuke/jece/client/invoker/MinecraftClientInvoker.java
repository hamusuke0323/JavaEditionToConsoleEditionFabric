package com.hamusuke.jece.client.invoker;

import com.hamusuke.jece.client.utils.StartupSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface MinecraftClientInvoker {
    StartupSoundPlayer getPlayer();
}
