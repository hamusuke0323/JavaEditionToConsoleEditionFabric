package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface ScreenInvoker {
    void renderDialogWindow(MatrixStack matrices, int x, int y, int width, int height);

    void renderSquare(MatrixStack matrices, int x, int y, int width, int height);

    void renderUpwardScrollIcon(MatrixStack matrices, int x, int y, float scale);

    void renderDownwardScrollIcon(MatrixStack matrices, int x, int y, float scale);

    void renderMinecraftTitle(MatrixStack matrices, boolean renderBackground);

    void renderBackgroundFillGradient(MatrixStack matrices);
}
