package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.invoker.DrawableHelperInvoker;
import com.hamusuke.jece.client.invoker.ScreenInvoker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.hamusuke.jece.client.util.CEUtil.DIALOG_WINDOW;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public class ScreenMixin extends DrawableHelper implements ScreenInvoker {
    private static final Identifier CREATIVE_INVENTORY_TABS = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    @Shadow
    @Nullable
    protected MinecraftClient client;

    public void renderDialogWindow(MatrixStack matrices, int x, int y, int width, int height) {
        if (width < 24 || height < 24) {
            throw new IllegalArgumentException("minimum size is 24 x 24");
        }
        this.client.getTextureManager().bindTexture(DIALOG_WINDOW);
        int ceilWidth = (int) Math.ceil((double) (width - 16) / 8);
        int ceilHeight = (int) Math.ceil((double) (height - 16) / 8);
        int right = x + 8 + ceilWidth * 8;
        int floor = y + 8 + ceilHeight * 8;
        this.blit32(matrices, x, y, 0, 0);
        this.blit32(matrices, x, floor, 0, 16);
        this.blit32(matrices, right, y, 16, 0);
        this.blit32(matrices, right, floor, 16, 16);
        for (int i = 0; i < ceilWidth; i++) {
            int field = x + 8 + i * 8;
            this.blit32(matrices, field, y, 8, 0);
            this.blit32(matrices, field, floor, 8, 16);
        }
        for (int i = 0; i < ceilHeight; i++) {
            int field = y + 8 + i * 8;
            this.blit32(matrices, x, field, 0, 8);
            this.blit32(matrices, right, field, 16, 8);
        }
        for (int i = 0; i < ceilWidth; i++) {
            for (int j = 0; j < ceilHeight; j++) {
                this.blit32(matrices, x + 8 + i * 8, y + 8 + j * 8, 8, 8);
            }
        }
    }

    private void blit32(MatrixStack matrices, int x, int y, int texX, int texY) {
        drawTexture(matrices, x, y, texX, texY, 8, 8, 32, 32);
    }

    public void renderSquare(MatrixStack matrices, int x, int y, int width, int height) {
        int i = BackgroundHelper.ColorMixer.getArgb(75, 90, 96, 240);
        this.fillGradient(matrices, x + 1, y + 1, x + i - 1, y + height - 1, i, i);

        int j = BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255);
        this.fillGradient(matrices, x, y + 1, x + 1, y + height - 1, j, j);
        this.fillGradient(matrices, x + i - 1, y + 1, x + i, y + height - 1, j, j);
        this.fillGradient(matrices, x + 1, y, x + i - 1, y + 1, j, j);
        this.fillGradient(matrices, x + 1, y + height - 1, x + i - 1, y + height, j, j);

        DrawableHelperInvoker invoker = (DrawableHelperInvoker) this;
        invoker.fillGradient(matrices, x + 0.5F, y + 0.5F, x + 1.5F, y + 1.5F, j, j);
        invoker.fillGradient(matrices, x + i - 1.5F, y + 0.5F, x + i - 0.5F, y + 1.5F, j, j);
        invoker.fillGradient(matrices, x + 0.5F, y + height - 1.5F, x + 1.5F, y + height - 0.5F, j, j);
        invoker.fillGradient(matrices, x + i - 1.5F, y + height - 1.5F, x + i - 0.5F, y + height - 0.5F, j, j);
    }

    public void renderUpwardScrollIcon(MatrixStack matrices, int x, int y, float scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0F);
        RenderSystem.scalef(scale, scale, scale);
        this.client.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
        this.drawTexture(matrices, 0, 0, 16, 83, 13, 7);
        RenderSystem.popMatrix();
    }

    public void renderDownwardScrollIcon(MatrixStack matrices, int x, int y, float scale) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0F);
        RenderSystem.scalef(scale, scale, scale);
        this.client.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
        this.drawTexture(matrices, 0, 0, 16, 92, 13, 7);
        RenderSystem.popMatrix();
    }
}
