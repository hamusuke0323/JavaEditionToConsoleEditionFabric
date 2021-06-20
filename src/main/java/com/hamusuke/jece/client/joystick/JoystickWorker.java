package com.hamusuke.jece.client.joystick;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class JoystickWorker {
    private final int stickId;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((runnable) -> new Thread(runnable, "JoystickWorkerThread"));
    private final AtomicReference<JoystickCallbacks.Stick> stickCallback = new AtomicReference<>((floatBuffer) -> {
    });
    private final AtomicReference<JoystickCallbacks.Button> buttonCallback = new AtomicReference<>((byteBuffer) -> {
    });

    public JoystickWorker(int stickId) {
        this.stickId = stickId;
    }

    public void setCallbacks(@NotNull JoystickCallbacks.Stick stickCallback, @NotNull JoystickCallbacks.Button buttonCallback) {
        Objects.requireNonNull(stickCallback, "stickCallback cannot be null");
        Objects.requireNonNull(buttonCallback, "buttonCallback cannot be null");

        this.stickCallback.set(stickCallback);
        this.buttonCallback.set(buttonCallback);
    }

    public void schedule() {
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (!GLFW.glfwJoystickPresent(this.stickId)) {
                this.close();
                return;
            }

            FloatBuffer floatBuffer = GLFW.glfwGetJoystickAxes(this.stickId);
            ByteBuffer byteBuffer = GLFW.glfwGetJoystickButtons(this.stickId);
            if (floatBuffer != null) {
                this.stickCallback.get().onMoveStick(floatBuffer);
            }
            if (byteBuffer != null) {
                this.buttonCallback.get().onPressButton(byteBuffer);
            }
        }, 0L, 5L, TimeUnit.MILLISECONDS);
    }

    public void close() {
        this.scheduledExecutorService.shutdown();
    }

    public int getStickId() {
        return this.stickId;
    }
}
