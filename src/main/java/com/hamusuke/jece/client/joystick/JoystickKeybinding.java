package com.hamusuke.jece.client.joystick;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hamusuke.jece.client.JECEClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class JoystickKeybinding implements Comparable<JoystickKeybinding> {
    private static final Map<String, JoystickKeybinding> keysById = Maps.newHashMap();
    private static final Map<JoystickInputUtil.Key, JoystickKeybinding> keyToBindings = Maps.newHashMap();
    private static final Set<String> keyCategories = Sets.newHashSet();
    private static final Map<String, Integer> categoryOrderMap = Util.make(Maps.newHashMap(), (hashMap) -> {
        hashMap.put("key.categories.movement", 1);
        hashMap.put("key.categories.gameplay", 2);
        hashMap.put("key.categories.inventory", 3);
        hashMap.put("key.categories.creative", 4);
        hashMap.put("key.categories.multiplayer", 5);
        hashMap.put("key.categories.ui", 6);
        hashMap.put("key.categories.misc", 7);
    });
    private final String translationKey;
    private final JoystickInputUtil.Key defaultKey;
    private final String category;
    private JoystickInputUtil.Key boundKey;
    private boolean pressed;
    private int timesPressed;
    private int timer;

    public static void onKeyPressed(JoystickInputUtil.Key key) {
        JoystickKeybinding joystickKeybinding = keyToBindings.get(key);
        if (joystickKeybinding != null) {
            if (joystickKeybinding.timer > 120) {
                ++joystickKeybinding.timesPressed;
                joystickKeybinding.timer = 0;
            }

            joystickKeybinding.timer++;
        }
    }

    @Nullable
    public static JoystickKeybinding getKeyBind(JoystickInputUtil.Key key) {
        return keyToBindings.get(key);
    }

    public int getTimer() {
        return this.timer;
    }

    public static void setKeyPressed(JoystickInputUtil.Key key, boolean pressed) {
        JoystickKeybinding joystickKeybinding = keyToBindings.get(key);
        if (joystickKeybinding != null) {
            joystickKeybinding.setPressed(pressed);
        }
    }

    public static void updatePressedStates() {
        if (JECEClient.joystickWorker.get() != null) {
            for (JoystickKeybinding joystickKeybinding : keysById.values()) {
                if (joystickKeybinding.boundKey.getCode() != -1) {
                    joystickKeybinding.setPressed(JoystickInputUtil.isJoystickKeyDown(JECEClient.joystickWorker.get().getStickId(), joystickKeybinding.boundKey.getCode()));
                }
            }
        }
    }

    public static void unpressAll() {
        for (JoystickKeybinding joystickKeybinding : keysById.values()) {
            joystickKeybinding.reset();
        }
    }

    public static void updateKeysByCode() {
        keyToBindings.clear();

        for (JoystickKeybinding joystickKeybinding : keysById.values()) {
            keyToBindings.put(joystickKeybinding.boundKey, joystickKeybinding);
        }
    }

    public JoystickKeybinding(String translationKey, int code, String category) {
        this.translationKey = translationKey;
        this.boundKey = JoystickInputUtil.JoyKeys.createFromCode(code);
        this.defaultKey = this.boundKey;
        this.category = category;
        keysById.put(translationKey, this);
        keyToBindings.put(this.boundKey, this);
        keyCategories.add(category);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        } else {
            --this.timesPressed;
            return true;
        }
    }

    private void reset() {
        this.timesPressed = 0;
        this.timer = 0;
        this.setPressed(false);
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public JoystickInputUtil.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setBoundKey(JoystickInputUtil.Key boundKey) {
        this.boundKey = boundKey;
    }

    public int compareTo(JoystickKeybinding joystickKeybinding) {
        return this.category.equals(joystickKeybinding.category) ? I18n.translate(this.translationKey).compareTo(I18n.translate(joystickKeybinding.translationKey)) : categoryOrderMap.get(this.category).compareTo(categoryOrderMap.get(joystickKeybinding.category));
    }

    public static Supplier<Text> getLocalizedName(String id) {
        JoystickKeybinding joystickKeybinding = keysById.get(id);
        return joystickKeybinding == null ? () -> new TranslatableText(id) : joystickKeybinding::getBoundKeyLocalizedText;
    }

    public boolean equals(JoystickKeybinding other) {
        return this.boundKey.equals(other.boundKey);
    }

    public boolean isUnbound() {
        return this.boundKey.equals(JoystickInputUtil.UNKNOWN_KEY);
    }

    public boolean matchesKey(int keyCode) {
        return keyCode != JoystickInputUtil.UNKNOWN_KEY.getCode() && this.boundKey.getCode() == keyCode;
    }

    public Text getBoundKeyLocalizedText() {
        return this.boundKey.getLocalizedText();
    }

    public boolean isDefault() {
        return this.boundKey.equals(this.defaultKey);
    }

    public String getBoundKeyTranslationKey() {
        return this.boundKey.getTranslationKey();
    }

    public void setPressed(boolean pressed) {
        if (this.timer == 0) {
            this.timer = 121;
        }

        this.pressed = pressed;

        if (!pressed) {
            this.timer = 0;
        }
    }
}
