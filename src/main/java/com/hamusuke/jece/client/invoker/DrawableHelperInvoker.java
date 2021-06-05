package com.hamusuke.jece.client.invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface DrawableHelperInvoker {
    void fillGradient(MatrixStack matrices, float xStart, float yStart, float xEnd, float yEnd, int colorStart, int colorEnd);
}
