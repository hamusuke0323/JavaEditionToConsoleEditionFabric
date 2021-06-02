package com.hamusuke.jece.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    private TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
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
        this.addButton(new ButtonWidget(this.width / 2 + 2, i + 72 + 12, 98, 20, new TranslatableText("menu.quit"), (buttonWidget) -> this.client.scheduleStop()));
        this.client.setConnectedToRealms(false);
        ci.cancel();
    }

    @Inject(method = "initWidgetsNormal", at = @At("HEAD"), cancellable = true)
    private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, y, 200, 20, new TranslatableText("menu.singleplayer"), (buttonWidget) -> this.client.openScreen(new SelectWorldScreen(this))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, y + spacingY, 200, 20, new TranslatableText("menu.multiplayer"), (buttonWidget) -> this.client.openScreen(this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this)), this.client.isMultiplayerEnabled() ? ButtonWidget.EMPTY : (buttonWidget, matrixStack, i, j) -> {
            if (!buttonWidget.active) {
                this.renderOrderedTooltip(matrixStack, this.client.textRenderer.wrapLines(new TranslatableText("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), i, j);
            }
        })).active = this.client.isMultiplayerEnabled();
        ci.cancel();
    }
}
