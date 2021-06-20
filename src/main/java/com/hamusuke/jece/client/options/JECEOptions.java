package com.hamusuke.jece.client.options;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hamusuke.jece.client.joystick.JoystickInputUtil;
import com.hamusuke.jece.client.joystick.JoystickKeybinding;
import com.hamusuke.jece.client.joystick.JoystickStickyKeybinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class JECEOptions {
    private final File configFilePath;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    public boolean displayPlayerOnScreen = true;
    public int autoSaveTicks = 4000;
    public final JoystickKeybinding keyJump;
    public final JoystickKeybinding keySneak;
    public final JoystickKeybinding keyInventory;
    public final JoystickKeybinding keyDrop;
    public final JoystickKeybinding keyUse;
    public final JoystickKeybinding keyAttack;
    public final JoystickKeybinding keyPickItem;
    public final JoystickKeybinding keyChat;
    public final JoystickKeybinding keyPlayerList;
    public final JoystickKeybinding keySocialInteractions;
    public final JoystickKeybinding keyScreenshot;
    public final JoystickKeybinding keyTogglePerspective;
    public final JoystickKeybinding keyHotbarLeft;
    public final JoystickKeybinding keyHotbarRight;
    public final JoystickKeybinding keySaveToolbarActivator;
    public final JoystickKeybinding keyLoadToolbarActivator;
    public final JoystickKeybinding[] keysAll;

    public JECEOptions(File configFilePath) {
        this.configFilePath = configFilePath;
        this.keyJump = new JoystickKeybinding("key.jump", GLFW.GLFW_GAMEPAD_BUTTON_A, "key.categories.movement");
        this.keySneak = new JoystickStickyKeybinding("key.sneak", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, "key.categories.movement", () -> true);
        this.keyInventory = new JoystickKeybinding("key.inventory", GLFW.GLFW_GAMEPAD_BUTTON_Y, "key.categories.inventory");
        this.keyDrop = new JoystickKeybinding("key.drop", GLFW.GLFW_GAMEPAD_BUTTON_B, "key.categories.inventory");
        this.keyUse = new JoystickKeybinding("key.use", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, "key.categories.gameplay");
        this.keyAttack = new JoystickKeybinding("key.attack", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, "key.categories.gameplay");
        this.keyPickItem = new JoystickKeybinding("key.pickItem", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, "key.categories.gameplay");
        this.keyChat = new JoystickKeybinding("key.chat", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, "key.categories.multiplayer");
        this.keyPlayerList = new JoystickKeybinding("key.playerlist", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, "key.categories.multiplayer");
        this.keySocialInteractions = new JoystickKeybinding("key.socialInteractions", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, "key.categories.multiplayer");
        this.keyScreenshot = new JoystickKeybinding("key.screenshot", JoystickInputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
        this.keyTogglePerspective = new JoystickKeybinding("key.togglePerspective", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB, "key.categories.misc");
        this.keySaveToolbarActivator = new JoystickKeybinding("key.saveToolbarActivator", JoystickInputUtil.UNKNOWN_KEY.getCode(), "key.categories.creative");
        this.keyLoadToolbarActivator = new JoystickKeybinding("key.loadToolbarActivator", JoystickInputUtil.UNKNOWN_KEY.getCode(), "key.categories.creative");
        this.keyHotbarLeft = new JoystickKeybinding("key.hotbar.left", GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, "key.categories.inventory");
        this.keyHotbarRight = new JoystickKeybinding("key.hotbar.right", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, "key.categories.inventory");
        this.keysAll = new JoystickKeybinding[]{this.keyAttack, this.keyUse, this.keyJump, this.keySneak, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySaveToolbarActivator, this.keyLoadToolbarActivator, this.keyHotbarLeft, this.keyHotbarRight};
    }

    public void setKeyCode(JoystickKeybinding key, JoystickInputUtil.Key code) {
        key.setBoundKey(code);
        this.write();
    }

    public void write() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.configFilePath);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(outputStreamWriter)
        ) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();

            jsonWriter.name("displayplayeronscreen").value(this.displayPlayerOnScreen);
            jsonWriter.name("autosaveticks").value(this.autoSaveTicks);

            for (JoystickKeybinding joystickKeybinding : this.keysAll) {
                jsonWriter.name("key_" + joystickKeybinding.getTranslationKey()).value(joystickKeybinding.getBoundKeyTranslationKey());
            }

            jsonWriter.endObject();
        } catch (Throwable e) {
            LOGGER.warn("Failed to save options", e);
        }
    }

    public void read() {
        if (this.configFilePath.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(this.configFilePath);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            ) {
                JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);

                if (jsonObject.has("displayplayeronscreen")) {
                    this.displayPlayerOnScreen = jsonObject.get("displayplayeronscreen").getAsBoolean();
                }

                if (jsonObject.has("autosaveticks")) {
                    this.autoSaveTicks = jsonObject.get("autosaveticks").getAsInt();
                    if (this.autoSaveTicks >= 0 && this.autoSaveTicks < 900) {
                        this.autoSaveTicks = 900;
                    }
                }

                for (JoystickKeybinding joystickKeybinding : this.keysAll) {
                    String string = "key_" + joystickKeybinding.getTranslationKey();
                    if (jsonObject.has(string)) {
                        joystickKeybinding.setBoundKey(JoystickInputUtil.fromTranslationKey(jsonObject.get(string).getAsString()));
                    }
                }

                JoystickKeybinding.updateKeysByCode();
            } catch (Throwable e) {
                LOGGER.warn("Failed to load options", e);
            }
        }
    }
}
