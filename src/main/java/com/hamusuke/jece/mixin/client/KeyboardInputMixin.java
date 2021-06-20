package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.event.KeyboardInputTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
@Environment(EnvType.CLIENT)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(boolean slowDown, CallbackInfo ci) {
        KeyboardInputTickEvent.EVENT.invoker().onTick((Input) (Object) this, slowDown);
    }
}
