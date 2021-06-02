package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.invoker.DrawableHelperInvoker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DrawableHelper.class)
@Environment(EnvType.CLIENT)
public class DrawableHelperMixin implements DrawableHelperInvoker {
    @Shadow
    private int zOffset;

    public void fillGradient(MatrixStack matrices, float xStart, float yStart, float xEnd, float yEnd, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = matrices.peek().getModel();

        float f = (float) (colorStart >> 24 & 255) / 255.0F;
        float g = (float) (colorStart >> 16 & 255) / 255.0F;
        float h = (float) (colorStart >> 8 & 255) / 255.0F;
        float i = (float) (colorStart & 255) / 255.0F;
        float j = (float) (colorEnd >> 24 & 255) / 255.0F;
        float k = (float) (colorEnd >> 16 & 255) / 255.0F;
        float l = (float) (colorEnd >> 8 & 255) / 255.0F;
        float m = (float) (colorEnd & 255) / 255.0F;
        int z = this.zOffset;
        bufferBuilder.vertex(matrix, xEnd, yStart, z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, xStart, yStart, z).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, xStart, yEnd, z).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, xEnd, yEnd, z).color(k, l, m, j).next();

        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
}
