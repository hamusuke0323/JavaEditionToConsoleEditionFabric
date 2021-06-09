package com.hamusuke.jece.client.gui.screen;

import com.hamusuke.jece.client.invoker.MinecraftClientInvoker;
import com.hamusuke.jece.client.invoker.ScreenInvoker;
import com.hamusuke.jece.client.util.CEUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ProgressBarScreen extends Screen {
    @Nullable
    private final WorldGenerationProgressTracker listener;
    private float progress;
    private Text title;
    private Text description;
    private final boolean noRenderingBar;

    public ProgressBarScreen(Text title, Text desc) {
        this(title, desc, null, false);
    }

    public ProgressBarScreen() {
        this(NarratorManager.EMPTY, NarratorManager.EMPTY, null, true);
    }

    public ProgressBarScreen(Text text, Text desc, @Nullable WorldGenerationProgressTracker tracker, boolean noRenderingBar) {
        super(NarratorManager.EMPTY);
        this.title = text;
        this.description = desc;
        this.listener = tracker;
        this.noRenderingBar = noRenderingBar;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public ProgressBarScreen description(Text description) {
        this.setDescription(description);
        return this;
    }

    public void setProgress(float progress) {
        this.progress = MathHelper.clamp(progress, 0.0F, 1.0F);
    }

    public ProgressBarScreen progress(float progress) {
        this.setProgress(progress);
        return this;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        GlStateManager.disableDepthTest();
        fill(matrices, 0, 0, this.width, this.height, -1);
        ((MinecraftClientInvoker) this.client).getPanorama().render(delta, 1.0F);
        this.client.getTextureManager().bindTexture(CEUtil.PANORAMA_OVERLAY_CE);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        ((ScreenInvoker) this).renderMinecraftTitle(matrices, false);

        if (this.listener != null) {
            int per = this.listener.getProgressPercentage();

            if (per >= 0 && per <= 30) {
                this.progress = MathHelper.clamp(((float) per) / 30.0F, 0.0F, 1.0F);
            } else if (per >= 31 && per <= 60) {
                this.progress = MathHelper.clamp(((float) per) / 60.0F, 0.0F, 1.0F);
            } else if (per >= 61 && per <= 90) {
                this.progress = MathHelper.clamp(((float) per) / 90.0F, 0.0F, 1.0F);
            } else if (per > 90) {
                this.progress = 0.0F;
            }

            this.renderProgressBar(matrices, this.width / 2 - this.width / 3, this.height / 2 + 11, this.width / 2 + this.width / 3, this.height / 2 + 20);
        } else if (((MinecraftClientInvoker) this.client).isCreateWorld() || !this.noRenderingBar) {
            this.renderProgressBar(matrices, this.width / 2 - this.width / 3, this.height / 2 + 11, this.width / 2 + this.width / 3, this.height / 2 + 20);
        }

        float scale = 2.0F;
        float x = (float) -this.textRenderer.getWidth(this.title.getString()) / 2;
        float y = -10.0F;
        float outlineSize = 0.4F;

        matrices.push();
        matrices.translate((double) this.width / 2, (double) this.height / 2, 0.0D);
        matrices.scale(scale, scale, scale);
        for (float offX = -outlineSize; offX <= outlineSize; offX += 0.1F) {
            for (float offY = -outlineSize; offY <= outlineSize; offY += 0.1F) {
                this.textRenderer.draw(matrices, this.title, x + offX, y + offY, 0);
            }
        }
        this.textRenderer.draw(matrices, this.title, x, y, 16777215);
        matrices.pop();
        this.textRenderer.draw(matrices, this.description, (float) (this.width / 2 - this.width / 3), (float) this.height / 2 + 2, 16777215);

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2) {
        int i = MathHelper.ceil((float) (x2 - x1) * this.progress) == 0 ? 1 : MathHelper.ceil((float) (x2 - x1) * this.progress);
        int j = BackgroundHelper.ColorMixer.getArgb(255, 140, 140, 140);
        int k = BackgroundHelper.ColorMixer.getArgb(255, 0, 255, 0);
        fill(matrices, x1, y1, x2, y2, j);
        fill(matrices, x1 + 1, y1 + 1, x1 + i, y2 - 1, k);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
}
