package com.hamusuke.jece.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffectType.class)
@Environment(EnvType.CLIENT)
public class StatusEffectTypeMixin {
    @Mutable
    @Shadow
    @Final
    private Formatting formatting;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void StatusEffectType(String enumName, int ordinal, Formatting format, CallbackInfo ci) {
        this.formatting = enumName.equalsIgnoreCase("beneficial") || enumName.equalsIgnoreCase("neutral") ? Formatting.GRAY : format;
    }
}
