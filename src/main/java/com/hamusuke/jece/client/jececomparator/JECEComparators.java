package com.hamusuke.jece.client.jececomparator;

import com.google.common.collect.Lists;
import com.hamusuke.jece.client.MainClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class JECEComparators {
    public static final List<JECEComparator> JECE_COMPARATORS = Lists.newArrayList();
    public static final JECEComparator TITLE_SCREEN = registerComparator(new Identifier(MainClient.MOD_ID, "textures/gui/jeceswitcher/titlescreen.png"), 3840, 1080, "jece.switchers.titlescreen", JECEStorage.TITLE_SCREEN_BOOLEAN);
    public static final JECEComparator ENCHANTMENT = registerComparator(new Identifier(MainClient.MOD_ID, "textures/gui/jeceswitcher/enchantment.png"), 1266, 388, "jece.switchers.enchantment", JECEStorage.ENCHANTMENT_BOOLEAN);

    private static JECEComparator registerComparator(Identifier illustration, int textureWidth, int textureHeight, String translationKey, AtomicBoolean setterGetter) {
        JECEComparator jeceComparator = new JECEComparator(illustration, textureWidth, textureHeight, new TranslatableText(translationKey), new TranslatableText(translationKey + ".desc"), setterGetter);
        JECE_COMPARATORS.add(jeceComparator);
        return jeceComparator;
    }

    @Environment(EnvType.CLIENT)
    public static class JECEStorage {
        public static final AtomicBoolean TITLE_SCREEN_BOOLEAN = new AtomicBoolean();
        public static final AtomicBoolean ENCHANTMENT_BOOLEAN = new AtomicBoolean();
    }
}
