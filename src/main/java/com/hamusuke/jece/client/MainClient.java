package com.hamusuke.jece.client;

import com.hamusuke.jece.client.gui.screen.JECESwitcherScreen;
import com.hamusuke.jece.client.jececomparator.JECEComparators;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "jece";
    public static boolean isFirst = true;

    public static final Identifier UI_BUTTON_HOVER_SOUND = new Identifier(MOD_ID, "ui.button.hover");
    public static final Identifier UI_BACKBUTTON_CLICK_SOUND = new Identifier(MOD_ID, "ui.backbutton.click");
    public static final Identifier UI_SLIDER_SLIDING_SOUND = new Identifier(MOD_ID, "ui.slider.sliding");
    public static final SoundEvent UI_BUTTON_HOVER = new SoundEvent(UI_BUTTON_HOVER_SOUND);
    public static final SoundEvent UI_BACKBUTTON_CLICK = new SoundEvent(UI_BACKBUTTON_CLICK_SOUND);
    public static final SoundEvent UI_SLIDER_SLIDING = new SoundEvent(UI_SLIDER_SLIDING_SOUND);

    public static KeyBinding OPEN_DEFAULT_CE_SWITCHER_SCREEN;
    public static File jeceConfigDir;

    public void onInitializeClient() {
        jeceConfigDir = FabricLoader.getInstance().getConfigDir().resolve("jece").toFile();
        if (!jeceConfigDir.exists() && jeceConfigDir.mkdir()) {
            LOGGER.info("jece config directory not found. made the directory.");
        }

        JECEComparators.read();

        Registry.register(Registry.SOUND_EVENT, UI_BUTTON_HOVER_SOUND, UI_BUTTON_HOVER);
        Registry.register(Registry.SOUND_EVENT, UI_BACKBUTTON_CLICK_SOUND, UI_BACKBUTTON_CLICK);
        Registry.register(Registry.SOUND_EVENT, UI_SLIDER_SLIDING_SOUND, UI_SLIDER_SLIDING);

        OPEN_DEFAULT_CE_SWITCHER_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("jece.keybind.open.switcher", 342, "jece.jece"));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (OPEN_DEFAULT_CE_SWITCHER_SCREEN.wasPressed()) {
                client.openScreen(new JECESwitcherScreen(client.currentScreen));
            }
        });
    }
}
