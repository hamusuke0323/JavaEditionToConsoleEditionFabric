package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.invoker.client.AdvancementsScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementsScreen.class)
@Environment(EnvType.CLIENT)
public class AdvancementsScreenMixin extends Screen implements AdvancementsScreenInvoker {
    @Nullable
    private Screen parent;

    private AdvancementsScreenMixin(Text title) {
        super(title);
    }

    public AdvancementsScreen setParentScreen(@Nullable Screen parent) {
        this.parent = parent;
        return (AdvancementsScreen) (Object) this;
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        this.onClose();
        if (this.parent == null) {
            this.client.mouse.lockCursor();
        }
        cir.setReturnValue(true);
        cir.cancel();
    }

    public void onClose() {
        this.client.getSoundManager().play(PositionedSoundInstance.master(JECEClient.UI_BACKBUTTON_CLICK, 1.0F));
        this.client.openScreen(this.parent);
    }
}
