package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.invoker.client.AbstractButtonWidgetInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButtonWidget.class)
@Environment(EnvType.CLIENT)
public class AbstractButtonWidgetMixin implements AbstractButtonWidgetInvoker {
    @Shadow
    private Text message;

    @Nullable
    private SoundEvent onPressSound;

    @Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
    public void playDownSound(SoundManager soundManager, CallbackInfo ci) {
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

    public AbstractButtonWidget setOnPressSound(@Nullable SoundEvent onPressSound) {
        this.onPressSound = onPressSound;
        return (AbstractButtonWidget) (Object) this;
    }

    @Nullable
    public SoundEvent getOnPressSound() {
        return this.onPressSound;
    }
}
