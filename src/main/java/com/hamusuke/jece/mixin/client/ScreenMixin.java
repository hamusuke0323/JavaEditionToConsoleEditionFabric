package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.gui.screen.JECESettingsScreen;
import com.hamusuke.jece.client.gui.screen.JECESwitcherScreen;
import com.hamusuke.jece.invoker.client.DrawableHelperInvoker;
import com.hamusuke.jece.invoker.client.ScreenInvoker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.hamusuke.jece.client.util.CEUtil.DIALOG_WINDOW;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public abstract class ScreenMixin extends DrawableHelper implements ScreenInvoker, ParentElement {
    private static final Identifier CREATIVE_INVENTORY_TABS = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier MINECRAFT_TITLE_TEXTURES_CE = new Identifier(JECE.MOD_ID, "textures/gui/title/minecraft.png");
    private static final Identifier MINECRAFT_TITLE_EDITION_CE = new Identifier(JECE.MOD_ID, "textures/gui/title/edition.png");

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Shadow
    protected TextRenderer textRenderer;

    @Shadow
    public int width;

    @Shadow
    public int height;

    @Shadow
    public abstract void renderBackground(MatrixStack matrices);

    @Shadow
    public abstract boolean shouldCloseOnEsc();

    @Shadow
    public abstract void onClose();

    @Inject(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"), cancellable = true)
    public void renderBackground(MatrixStack matrices, int vOffset, CallbackInfo ci) {
        ci.cancel();
    }

    public void renderBackgroundFillGradient(MatrixStack matrices) {
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
    }

    public void renderMinecraftTitle(MatrixStack matrices, boolean renderBackground) {
        if (renderBackground) {
            this.renderBackgroundFillGradient(matrices);
            this.renderBackground(matrices);
        }
        int x = this.width / 2 - 73;
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.8F, 0.8F, 0.8F);
        this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES_CE);
        RenderSystem.translatef(x, 27, 0);
        drawTexture(matrices, 0, 0, 0, 0, 155, 44);
        RenderSystem.translatef(155, 0, 0);
        drawTexture(matrices, 0, 0, 0, 45, 155, 44);
        RenderSystem.popMatrix();

        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.8F, 0.8F, 0.8F);
        this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION_CE);
        RenderSystem.translatef(x + 87, 64, 0);
        drawTexture(matrices, 0, 0, 0.0F, 0.0F, 98, 14, 128, 16);
        RenderSystem.popMatrix();
    }

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
        int i = BackgroundHelper.ColorMixer.getArgb(240, 75, 90, 96);
        this.fillGradient(matrices, x + 1, y + 1, x + width - 1, y + height - 1, i, i);

        int j = BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255);
        this.fillGradient(matrices, x, y + 1, x + 1, y + height - 1, j, j);
        this.fillGradient(matrices, x + width - 1, y + 1, x + width, y + height - 1, j, j);
        this.fillGradient(matrices, x + 1, y, x + width - 1, y + 1, j, j);
        this.fillGradient(matrices, x + 1, y + height - 1, x + width - 1, y + height, j, j);

        DrawableHelperInvoker invoker = (DrawableHelperInvoker) this;
        invoker.fillGradient(matrices, x + 0.5F, y + 0.5F, x + 1.5F, y + 1.5F, j, j);
        invoker.fillGradient(matrices, x + width - 1.5F, y + 0.5F, x + width - 0.5F, y + 1.5F, j, j);
        invoker.fillGradient(matrices, x + 0.5F, y + height - 1.5F, x + 1.5F, y + height - 0.5F, j, j);
        invoker.fillGradient(matrices, x + width - 1.5F, y + height - 1.5F, x + width - 0.5F, y + height - 0.5F, j, j);
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

    @Inject(method = "renderOrderedTooltip", at = @At("HEAD"), cancellable = true)
    private void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, CallbackInfo ci) {
        if (!lines.isEmpty()) {
            int i = 0;
            for (OrderedText orderedText : lines) {
                int j = this.textRenderer.getWidth(orderedText);
                if (j > i) {
                    i = j;
                }
            }

            int k = x + 12;
            int l = y - 12;
            int n = 8;
            if (lines.size() > 1) {
                n += 2 + (lines.size() - 1) * 10;
            }

            if (k + i > this.width) {
                k -= 28 + i;
            }

            if (l + n + 6 > this.height) {
                l = this.height - n - 6;
            }

            matrices.push();
            matrices.translate(0.0D, 0.0D, 400.0D);
            Matrix4f matrix4f = matrices.peek().getModel();
            this.renderSquare(matrices, k - 3, l - 6, i + 6, n + 12);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

            for (int s = 0; s < lines.size(); ++s) {
                OrderedText orderedText2 = lines.get(s);
                if (orderedText2 != null) {
                    this.textRenderer.draw(orderedText2, (float) k, (float) l, -1, true, matrix4f, immediate, false, 0, 15728880);
                }

                if (s == 0) {
                    l += 2;
                }

                l += 10;
            }

            immediate.draw();
            matrices.pop();
        }
        ci.cancel();
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (JECEClient.OPEN_SETTINGS_SCREEN.matchesKey(keyCode, scanCode) && !(this.client.currentScreen instanceof JECESettingsScreen) && !(this.client.currentScreen instanceof JECESwitcherScreen)) {
            this.client.openScreen(new JECESettingsScreen(this.client.currentScreen));
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    public boolean joystickButtonPressed(int key) {
        if (key == 0 && this.shouldCloseOnEsc()) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(JECEClient.UI_BACKBUTTON_CLICK, 1.0F));
            this.onClose();
            return true;
        } else {
            return false;
        }
    }
}
