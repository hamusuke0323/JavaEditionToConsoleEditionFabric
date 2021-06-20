package com.hamusuke.jece.client.joystick;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Lazy;
import org.lwjgl.glfw.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public class JoystickInputUtil {
    public static final JoystickInputUtil.Key UNKNOWN_KEY = JoyKeys.createFromCode(-1);

    public static JoystickInputUtil.Key fromKeyCode(int keyCode) {
        return JoystickInputUtil.JoyKeys.createFromCode(keyCode);
    }

    public static JoystickInputUtil.Key fromTranslationKey(String translationKey) {
        if (JoystickInputUtil.Key.KEYS.containsKey(translationKey)) {
            return JoystickInputUtil.Key.KEYS.get(translationKey);
        } else {
            if (translationKey.startsWith(JoyKeys.name)) {
                String string = translationKey.substring(JoyKeys.name.length() + 1);
                return JoyKeys.createFromCode(Integer.parseInt(string));
            }
            throw new IllegalArgumentException("Unknown key name: " + translationKey);
        }
    }

    public static boolean isJoystickKeyDown(int jid, int index) {
        ByteBuffer bb = GLFW.glfwGetJoystickButtons(jid);
        if (bb != null) {
            try {
                byte b = bb.get(index);
                return b == (byte) 1;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        }

        return false;
    }

    public static boolean isStickForward(int jid) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
        if (buffer != null) {
            try {
                float f = buffer.get(1);
                return f <= -0.4;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isStickBack(int jid) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
        if (buffer != null) {
            try {
                float f = buffer.get(1);
                return f >= 0.4;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isStickLeft(int jid) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
        if (buffer != null) {
            try {
                float f = buffer.get(0);
                return f <= -0.4;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isStickRight(int jid) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
        if (buffer != null) {
            try {
                float f = buffer.get(0);
                return f >= 0.4;
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static float getStickAxes(int jid, int stick) {
        FloatBuffer buffer = GLFW.glfwGetJoystickAxes(jid);
        if (buffer != null) {
            try {
                return buffer.get(stick);
            } catch (IndexOutOfBoundsException e) {
                return 0.0F;
            }
        } else {
            return 0.0F;
        }
    }

    @Environment(EnvType.CLIENT)
    public static final class Key {
        private final String translationKey;
        private final int code;
        private final Lazy<Text> localizedText;
        private static final Map<String, JoystickInputUtil.Key> KEYS = Maps.newHashMap();

        private Key(String translationKey, int code) {
            this.translationKey = translationKey;
            this.code = code;
            this.localizedText = new Lazy<>(() -> JoyKeys.textTranslator.apply(code, translationKey));
            KEYS.put(translationKey, this);
        }

        public int getCode() {
            return this.code;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }

        public Text getLocalizedText() {
            return this.localizedText.get();
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other != null && this.getClass() == other.getClass()) {
                JoystickInputUtil.Key key = (JoystickInputUtil.Key) other;
                return this.code == key.code;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(this.code);
        }

        public String toString() {
            return this.translationKey;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class JoyKeys {
        private static final String name = "joystick.key";
        private static final Int2ObjectMap<JoystickInputUtil.Key> map = new Int2ObjectOpenHashMap<>();
        private static final BiFunction<Integer, String, Text> textTranslator = (integer, string) -> new TranslatableText(string);

        private static void registerInput(String translationKey, int keyCode) {
            JoystickInputUtil.Key key = new JoystickInputUtil.Key(translationKey, keyCode);
            map.put(keyCode, key);
        }

        public static JoystickInputUtil.Key createFromCode(int code) {
            return map.computeIfAbsent(code, (codex) -> {
                String string = name + "." + codex;
                return new JoystickInputUtil.Key(string, codex);
            });
        }

        static {
            registerInput(name + "." + "a", GLFW.GLFW_GAMEPAD_BUTTON_A);
            registerInput(name + "." + "b", GLFW.GLFW_GAMEPAD_BUTTON_B);
            registerInput(name + "." + "x", GLFW.GLFW_GAMEPAD_BUTTON_X);
            registerInput(name + "." + "y", GLFW.GLFW_GAMEPAD_BUTTON_Y);
            registerInput(name + "." + "left_bumper", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
            registerInput(name + "." + "right_bumper", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
            registerInput(name + "." + "back", GLFW.GLFW_GAMEPAD_BUTTON_BACK);
            registerInput(name + "." + "start", GLFW.GLFW_GAMEPAD_BUTTON_START);
            registerInput(name + "." + "guide", GLFW.GLFW_GAMEPAD_BUTTON_GUIDE);
            registerInput(name + "." + "left_thumb", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
            registerInput(name + "." + "right_thumb", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
            registerInput(name + "." + "dpad_up", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
            registerInput(name + "." + "dpad_right", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
            registerInput(name + "." + "dpad_down", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
            registerInput(name + "." + "dpad.left", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
        }
    }
}
