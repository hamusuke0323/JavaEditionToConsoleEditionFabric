package com.hamusuke.jece.client.joystick;

import com.hamusuke.jece.client.gui.JoystickElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public class JoystickListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final JoystickWorker joystickWorker;
    private final MinecraftClient client;
    private final SmoothUtil xSmoother = new SmoothUtil();
    private final SmoothUtil ySmoother = new SmoothUtil();
    private double xVelocity;
    private double yVelocity;
    private double lastLookTime = Double.MIN_VALUE;
    private Throwable throwableQueue;

    public JoystickListener(MinecraftClient client, JoystickWorker joystickWorker) {
        this.client = client;
        this.joystickWorker = joystickWorker;
        this.registerCallbacks();
    }

    private void onPressButton(ByteBuffer bb) {
        while (bb.hasRemaining()) {
            int index = bb.position();
            byte b = bb.get();
            if (b == (byte) 1) {
                if (this.client.currentScreen == null) {
                    JoystickKeybinding.setKeyPressed(JoystickInputUtil.fromKeyCode(index), true);
                    JoystickKeybinding.onKeyPressed(JoystickInputUtil.fromKeyCode(index));
                } else {
                    JoystickInputUtil.Key joystickInputUtil = JoystickInputUtil.fromKeyCode(index);
                    JoystickKeybinding.setKeyPressed(joystickInputUtil, true);
                    JoystickKeybinding.onKeyPressed(joystickInputUtil);
                    JoystickKeybinding joystickKeybinding = JoystickKeybinding.getKeyBind(joystickInputUtil);
                    if (joystickKeybinding != null && joystickKeybinding.getTimer() > 120) {
                        ((JoystickElement) this.client.currentScreen).joystickButtonPressed(index);
                    }
                }
            } else {
                JoystickKeybinding.setKeyPressed(JoystickInputUtil.fromKeyCode(index), false);
            }
        }
    }

    private void registerCallbacks() {
        this.joystickWorker.setCallbacks((s) -> {
            try {
                float LX = s.get(0);
                float LY = s.get(1);
                float RX = s.get(2);
                float RY = s.get(3);
                if (Math.abs(LX) >= 0.4F) {
                    this.client.execute(() -> this.joystickLStickCallback(LX, 0.0D));
                }
                if (Math.abs(LY) >= 0.4F) {
                    this.client.execute(() -> this.joystickLStickCallback(0.0D, LY));
                }
                if (Math.abs(RX) >= 0.4F) {
                    this.client.execute(() -> this.joystickRStickCallback(RX, 0.0D));
                }
                if (Math.abs(RY) >= 0.4F) {
                    this.client.execute(() -> this.joystickRStickCallback(0.0D, RY));
                }
            } catch (IndexOutOfBoundsException e) {
                if (this.throwableQueue == null) {
                    LOGGER.error("Couldn't get axes of stick.");
                    this.throwableQueue = e;
                }
            }
        }, (b) -> this.client.execute(() -> this.onPressButton(b)));
    }

    private void joystickLStickCallback(double x, double y) {
        if (this.client.currentScreen != null) {
            ((JoystickElement) this.client.currentScreen).joystickLStickMoved(x, y);
        }
    }

    private void joystickRStickCallback(double x, double y) {
        if (this.client.currentScreen == null) {
            if (this.client.isWindowFocused()) {
                this.xVelocity = x * 5.0D;
                this.yVelocity = y * 5.0D;
            }

            this.updatePlayerLook();
        } else {
            ((JoystickElement) this.client.currentScreen).joystickRStickMoved(x, y);
        }
    }

    private void updatePlayerLook() {
        double d0 = GlfwUtil.getTime();
        double d1 = d0 - this.lastLookTime;
        this.lastLookTime = d0;

        if (this.client.isWindowFocused()) {
            double d4 = this.client.options.mouseSensitivity + (double) 0.2F;
            double d5 = d4 * d4 * d4 * 8.0D;
            double d2;
            double d3;

            if (this.client.options.smoothCameraEnabled) {
                double d6 = this.xSmoother.smooth(this.xVelocity * d5, d1 * d5);
                double d7 = this.ySmoother.smooth(this.yVelocity * d5, d1 * d5);
                d2 = d6;
                d3 = d7;
            } else {
                this.xSmoother.clear();
                this.ySmoother.clear();
                d2 = this.xVelocity * d5;
                d3 = this.yVelocity * d5;
            }

            this.xVelocity = 0.0D;
            this.yVelocity = 0.0D;
            this.client.getTutorialManager().onUpdateMouse(d2, d3);

            if (this.client.player != null) {
                this.client.player.changeLookDirection(d2, d3 * (double) (this.client.options.invertYMouse ? -1 : 1));
            }
        } else {
            this.xVelocity = 0.0D;
            this.yVelocity = 0.0D;
        }
    }
}
