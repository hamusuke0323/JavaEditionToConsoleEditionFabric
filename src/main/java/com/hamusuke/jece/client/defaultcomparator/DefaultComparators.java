package com.hamusuke.jece.client.defaultcomparator;

import com.google.common.collect.Lists;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultComparators {
    public static final List<DefaultComparator> DEFAULT_COMPARATORS = Lists.newArrayList();
    public static final AtomicBoolean TITLE_SCREEN_DEFAULT = new AtomicBoolean();
    public static final DefaultComparator TITLE_SCREEN = register(null, 0, 0, new TranslatableText("jece.def.titlescreen"), null, () -> TITLE_SCREEN_DEFAULT.set(true), () -> TITLE_SCREEN_DEFAULT.set(false), TITLE_SCREEN_DEFAULT::get);
    public static final AtomicBoolean ENCHANTMENT_DEFAULT = new AtomicBoolean();
    public static final DefaultComparator ENCHANTMENT = register(null, 0, 0, new TranslatableText("jece.def.enchantment"), null, () -> ENCHANTMENT_DEFAULT.set(true), () -> ENCHANTMENT_DEFAULT.set(false), ENCHANTMENT_DEFAULT::get);

    private static DefaultComparator register(Identifier illustration, int textureWidth, int textureHeight, Text title, Text description, Runnable onPressUseDefault, Runnable onPressUseConsoleEdition, DefaultComparator.DefaultBoolean defaultBoolean) {
        DefaultComparator defaultComparator = new DefaultComparator(illustration, textureWidth, textureHeight, title, description, onPressUseDefault, onPressUseConsoleEdition, defaultBoolean);
        DEFAULT_COMPARATORS.add(defaultComparator);
        return defaultComparator;
    }
}
