package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.jececomparator.JECEComparators;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantmentMixin {
    @Shadow
    public abstract String getTranslationKey();

    @Shadow
    public abstract boolean isCursed();

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void getName(int level, CallbackInfoReturnable<Text> cir) {
        if (!JECEComparators.ENCHANTMENT.isJESelected()) {
            MutableText mutableText = new TranslatableText(this.getTranslationKey());
            if (this.isCursed()) {
                mutableText.formatted(Formatting.RED);
            } else {
                mutableText.formatted(Formatting.WHITE);
            }
            mutableText.append(" ").append(new TranslatableText("enchantment.level." + level));

            cir.setReturnValue(mutableText);
            cir.cancel();
        }
    }
}
