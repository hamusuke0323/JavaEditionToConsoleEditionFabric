package com.hamusuke.jece.client;

import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.gui.screen.JECESettingsScreen;
import com.hamusuke.jece.client.gui.screen.ProgressBarScreen;
import com.hamusuke.jece.client.jececomparator.JECEComparators;
import com.hamusuke.jece.client.options.JECEOptions;
import com.hamusuke.jece.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class JECEClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean isFirst = true;
    public static final Identifier UI_BUTTON_HOVER_SOUND = new Identifier(JECE.MOD_ID, "ui.button.hover");
    public static final Identifier UI_BACKBUTTON_CLICK_SOUND = new Identifier(JECE.MOD_ID, "ui.backbutton.click");
    public static final Identifier UI_SLIDER_SLIDING_SOUND = new Identifier(JECE.MOD_ID, "ui.slider.sliding");
    public static final SoundEvent UI_BUTTON_HOVER = new SoundEvent(UI_BUTTON_HOVER_SOUND);
    public static final SoundEvent UI_BACKBUTTON_CLICK = new SoundEvent(UI_BACKBUTTON_CLICK_SOUND);
    public static final SoundEvent UI_SLIDER_SLIDING = new SoundEvent(UI_SLIDER_SLIDING_SOUND);
    public static KeyBinding OPEN_SETTINGS_SCREEN;
    public static File jeceConfigDir;
    public static JECEOptions jeceOptions;
    private static final AtomicReference<Screen> current = new AtomicReference<>();

    public void onInitializeClient() {
        jeceConfigDir = FabricLoader.getInstance().getConfigDir().resolve("jece").toFile();
        if (!jeceConfigDir.exists() && jeceConfigDir.mkdir()) {
            LOGGER.info("jece config directory not found. made the directory.");
        }

        jeceOptions = new JECEOptions(new File(jeceConfigDir, "config.json"));
        jeceOptions.read();

        JECEComparators.read();

        Registry.register(Registry.SOUND_EVENT, UI_BUTTON_HOVER_SOUND, UI_BUTTON_HOVER);
        Registry.register(Registry.SOUND_EVENT, UI_BACKBUTTON_CLICK_SOUND, UI_BACKBUTTON_CLICK);
        Registry.register(Registry.SOUND_EVENT, UI_SLIDER_SLIDING_SOUND, UI_SLIDER_SLIDING);

        OPEN_SETTINGS_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("jece.keybind.open.settings", 342, "jece.jece"));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (OPEN_SETTINGS_SCREEN.wasPressed()) {
                client.openScreen(new JECESettingsScreen(client.currentScreen));
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.AUTO_SAVE_PACKET_ID, (client, handler, buf, responseSender) -> {
            if (!(client.currentScreen instanceof ProgressBarScreen)) {
                current.set(client.currentScreen);
            }
            Text saveLevel = new TranslatableText("menu.savelevel");
            Text text = buf.readText();
            float progress = buf.readFloat();
            boolean isDedicatedServer = buf.readBoolean();

            if (isDedicatedServer) {
                if (client.currentScreen instanceof ProgressBarScreen) {
                    ((ProgressBarScreen) client.currentScreen).progress(progress);
                } else {
                    client.send(() -> client.openScreen(new ProgressBarScreen(new TranslatableText("menu.dedicated.savelevel")).progress(progress)));
                }
            } else {
                if (client.currentScreen instanceof ProgressBarScreen) {
                    ((ProgressBarScreen) client.currentScreen).description(text).progress(progress);
                } else {
                    client.send(() -> client.openScreen(new ProgressBarScreen(saveLevel, text).progress(progress)));
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(NetworkManager.AUTO_SAVE_END_PACKET_ID, (client, handler, buf, responseSender) -> client.send(() -> client.openScreen(current.get())));
    }
}
