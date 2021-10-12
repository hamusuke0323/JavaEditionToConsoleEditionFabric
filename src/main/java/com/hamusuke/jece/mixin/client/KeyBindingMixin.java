package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.joystick.JoystickKeybinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
@Environment(EnvType.CLIENT)
public class KeyBindingMixin {
    @Inject(method = "updatePressedStates", at = @At("HEAD"))
    private static void updatePressedStates(CallbackInfo ci) {
        JoystickKeybinding.updatePressedStates();
    }

    @Inject(method = "unpressAll", at = @At("HEAD"))
    private static void unpressAll(CallbackInfo ci) {
        JoystickKeybinding.unpressAll();
    }
}
