package com.hamusuke.jece.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Map;

@Mixin(PotionUtil.class)
@Environment(EnvType.CLIENT)
public class PotionUtilMixin {
    @Inject(method = "buildTooltip", at = @At("HEAD"), cancellable = true)
    private static void buildTooltip(ItemStack stack, List<Text> list, float f, CallbackInfo ci) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(stack);
        List<Pair<EntityAttribute, EntityAttributeModifier>> list3 = Lists.newArrayList();

        if (!list2.isEmpty()) {
            for (StatusEffectInstance statusEffectInstance : list2) {
                TranslatableText translatableText = new TranslatableText(statusEffectInstance.getTranslationKey());
                StatusEffect effect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = effect.getAttributeModifiers();

                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier entityAttributeModifier = entry.getValue();
                        EntityAttributeModifier entityAttributeModifier2 = new EntityAttributeModifier(entityAttributeModifier.getName(), effect.adjustModifierAmount(statusEffectInstance.getAmplifier(), entityAttributeModifier), entityAttributeModifier.getOperation());
                        list3.add(new Pair<>(entry.getKey(), entityAttributeModifier2));
                    }
                }

                if (statusEffectInstance.getAmplifier() > 0) {
                    translatableText = new TranslatableText("potion.withAmplifier", translatableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
                }

                if (statusEffectInstance.getDuration() > 20) {
                    translatableText = new TranslatableText("potion.withDuration", translatableText, StatusEffectUtil.durationToString(statusEffectInstance, f));
                }

                list.add(translatableText.formatted(effect.getType().getFormatting()));
            }
        }

        if (!list3.isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add((new TranslatableText("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));

            for (Pair<EntityAttribute, EntityAttributeModifier> pair : list3) {
                EntityAttributeModifier entityAttributeModifier3 = pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double g;
                if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    g = entityAttributeModifier3.getValue();
                } else {
                    g = entityAttributeModifier3.getValue() * 100.0D;
                }

                if (d > 0.0D) {
                    list.add((new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.GRAY));
                } else if (d < 0.0D) {
                    g *= -1.0D;
                    list.add((new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.RED));
                }
            }
        }

        ci.cancel();
    }
}
