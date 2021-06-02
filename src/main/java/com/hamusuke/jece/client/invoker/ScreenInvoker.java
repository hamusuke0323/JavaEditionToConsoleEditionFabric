package com.hamusuke.jece.client.invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface ScreenInvoker {
    void renderDialogWindow(MatrixStack matrices, int x, int y, int width, int height);

    void renderSquare(MatrixStack matrices, int l1, int i2, int i, int k);

    void renderUpwardScrollIcon(MatrixStack matrices, int x, int y, float scale);

    void renderDownwardScrollIcon(MatrixStack matrices, int x, int y, float scale);
}
