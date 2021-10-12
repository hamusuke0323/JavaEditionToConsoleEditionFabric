package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.invoker.client.ClickableWidgetInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
@Environment(EnvType.CLIENT)
public class ClickableWidgetMixin implements ClickableWidgetInvoker {
    @Shadow
    private Text message;

    @Shadow
    public boolean active;
    @Nullable
    private SoundEvent onPressSound;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;queueNarration(I)V"))
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.active) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(JECEClient.UI_BUTTON_HOVER, 1.0F));
        }
    }

    @Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
    private void playDownSound(SoundManager soundManager, CallbackInfo ci) {
        if (this.onPressSound == null) {
            if (this.message instanceof TranslatableText) {
                String key = ((TranslatableText) this.message).getKey();
                if (key.equals("gui.back") || key.equals("gui.cancel") || key.equals("gui.done")) {
                    soundManager.play(PositionedSoundInstance.master(JECEClient.UI_BACKBUTTON_CLICK, 1.0F));
                    ci.cancel();
                }
            }
        } else {
            soundManager.play(PositionedSoundInstance.master(this.onPressSound, 1.0F));
            ci.cancel();
        }
    }

    public ClickableWidget setOnPressSound(@Nullable SoundEvent onPressSound) {
        this.onPressSound = onPressSound;
        return (ClickableWidget) (Object) this;
    }

    @Nullable
    public SoundEvent getOnPressSound() {
        return this.onPressSound;
    }
}
