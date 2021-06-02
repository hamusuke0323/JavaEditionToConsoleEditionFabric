package com.hamusuke.jece.client.invoker;

import net.minecraft.client.util.math.MatrixStack;

public interface DrawableHelperInvoker {
    void fillGradient(MatrixStack matrices, float xStart, float yStart, float xEnd, float yEnd, int colorStart, int colorEnd);
}
