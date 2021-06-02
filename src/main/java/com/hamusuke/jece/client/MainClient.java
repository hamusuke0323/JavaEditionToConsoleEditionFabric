package com.hamusuke.jece.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.realms.KeyCombo;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
    public static final String MOD_ID = "jece";
    public static boolean isFirst = true;

    public static final Identifier UI_BUTTON_HOVER_SOUND = new Identifier(MOD_ID, "ui.button.hover");
    public static final Identifier UI_BACKBUTTON_CLICK_SOUND = new Identifier(MOD_ID, "ui.backbutton.click");
    public static final Identifier UI_SLIDER_SLIDING_SOUND = new Identifier(MOD_ID, "ui.slider.sliding");
    public static final SoundEvent UI_BUTTON_HOVER = new SoundEvent(UI_BUTTON_HOVER_SOUND);
    public static final SoundEvent UI_BACKBUTTON_CLICK = new SoundEvent(UI_BACKBUTTON_CLICK_SOUND);
    public static final SoundEvent UI_SLIDER_SLIDING = new SoundEvent(UI_SLIDER_SLIDING_SOUND);

    public void onInitializeClient() {
        Registry.register(Registry.SOUND_EVENT, UI_BUTTON_HOVER_SOUND, UI_BUTTON_HOVER);
        Registry.register(Registry.SOUND_EVENT, UI_BACKBUTTON_CLICK_SOUND, UI_BACKBUTTON_CLICK);
        Registry.register(Registry.SOUND_EVENT, UI_SLIDER_SLIDING_SOUND, UI_SLIDER_SLIDING);

    }
}
