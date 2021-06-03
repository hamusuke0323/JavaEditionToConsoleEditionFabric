package com.hamusuke.jece.client.invoker;

import com.hamusuke.jece.client.util.StartupSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

@Environment(EnvType.CLIENT)
public interface MinecraftClientInvoker {
    StartupSoundPlayer getPlayer();

    RotatingCubeMapRenderer getPanorama();
}
