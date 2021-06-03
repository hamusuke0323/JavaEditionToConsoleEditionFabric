package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.MainClient;
import com.hamusuke.jece.client.gui.screen.ConfirmScreenCE;
import com.hamusuke.jece.client.invoker.MinecraftClientInvoker;
import com.hamusuke.jece.client.invoker.ScreenInvoker;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(TitleScreen.class)
@Environment(EnvType.CLIENT)
public abstract class TitleScreenMixin extends Screen {
    @Shadow
    @Nullable
    private String splashText;

    @Shadow
    private int copyrightTextWidth;

    @Shadow
    private int copyrightTextX;

    @Shadow
    protected abstract void initWidgetsDemo(int y, int spacingY);

    @Shadow
    protected abstract void initWidgetsNormal(int y, int spacingY);

    private static boolean isDefault;
    private static final Identifier PANORAMA_OVERLAY_CE = new Identifier(MainClient.MOD_ID, "textures/gui/title/background/panorama_overlay.png");

    private TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        if (!isDefault) {
            if (this.splashText == null) {
                this.splashText = this.client.getSplashTextLoader().get();
            }

            this.copyrightTextWidth = this.textRenderer.getWidth("Copyright Mojang AB. Do not distribute!");
            this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
            int i = this.height / 4 + 48;
            if (this.client.isDemo()) {
                this.initWidgetsDemo(i, 24);
            } else {
                this.initWidgetsNormal(i, 24);
            }

            this.addButton(new ButtonWidget(this.width / 2 - 100, i + 72 + 12, 98, 20, new TranslatableText("menu.options"), (buttonWidget) -> this.client.openScreen(new OptionsScreen(this, this.client.options))));
            this.addButton(new ButtonWidget(this.width / 2 + 2, i + 72 + 12, 98, 20, new TranslatableText("menu.quit"), (buttonWidget) -> this.client.openScreen(new ConfirmScreenCE((TitleScreen) (Object) this, new TranslatableText("menu.quit"), new TranslatableText("menu.quit.message"), (b) -> this.client.openScreen((TitleScreen) (Object) this), (b) -> this.client.scheduleStop()))));
            this.client.setConnectedToRealms(false);
            ci.cancel();
        }
    }

    @Inject(method = "initWidgetsNormal", at = @At("HEAD"), cancellable = true)
    private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        if (!isDefault) {
            this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), (buttonWidget) -> this.client.openScreen(new SelectWorldScreen(this))));
            this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY, 200, 20, new TranslatableText("menu.multiplayer"), (buttonWidget) -> this.client.openScreen(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this)), this.client.isMultiplayerEnabled() ? ButtonWidget.EMPTY : (buttonWidget, matrixStack, i, j) -> {
                if (!buttonWidget.active) {
                    this.renderOrderedTooltip(matrixStack, this.client.textRenderer.wrapLines(new TranslatableText("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), i, j);
                }
            })).active = this.client.isMultiplayerEnabled();
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!isDefault) {
            fill(matrices, 0, 0, this.width, this.height, -1);
            ((MinecraftClientInvoker) this.client).getPanorama().render(delta, 1.0F);
            int i = this.width / 2 - 137;
            this.client.getTextureManager().bindTexture(PANORAMA_OVERLAY_CE);
            drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

            ((ScreenInvoker) this).renderMinecraftTitle(matrices, false);

            if (this.client.currentScreen != this) {
                ci.cancel();
            }

            if (this.splashText != null) {
                RenderSystem.pushMatrix();
                RenderSystem.translatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
                RenderSystem.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
                float h = 1.8F - MathHelper.abs(MathHelper.sin((float) (Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
                h = h * 100.0F / (float) (this.textRenderer.getWidth(this.splashText) + 32);
                RenderSystem.scalef(h, h, h);
                drawCenteredString(matrices, this.textRenderer, this.splashText, 0, -8, 16776960);
                RenderSystem.popMatrix();
            }

            String string = "Minecraft " + SharedConstants.getGameVersion().getName();
            if (this.client.isDemo()) {
                string = string + " Demo";
            } else {
                string = string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
            }

            if (this.client.isModded()) {
                string = string + I18n.translate("menu.modded", new Object[0]);
            }

            drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215);
            drawStringWithShadow(matrices, this.textRenderer, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 16777215);
            if (mouseX > this.copyrightTextX && mouseX < this.copyrightTextX + this.copyrightTextWidth && mouseY > this.height - 10 && mouseY < this.height) {
                fill(matrices, this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 16777215);
            }

            super.render(matrices, mouseX, mouseY, delta);
            ci.cancel();
        }
    }
}
