package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.invoker.client.CreditsScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreditsScreen.class)
@Environment(EnvType.CLIENT)
public class CreditsScreenMixin extends Screen implements CreditsScreenInvoker {
    @Nullable
    private Screen parent;

    private CreditsScreenMixin(Text title) {
        super(title);
    }

    public CreditsScreen setParentScreen(Screen parent) {
        this.parent = parent;
        return (CreditsScreen) (Object) this;
    }

    @ModifyArg(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), index = 0)
    private Screen close(Screen screen) {
        return this.parent;
    }
}
