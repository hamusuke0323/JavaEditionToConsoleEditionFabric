package com.hamusuke.jece.client.joystick;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

@Environment(EnvType.CLIENT)
public interface JoystickCallbacks {
    @Environment(EnvType.CLIENT)
    interface Stick {
        void onMoveStick(FloatBuffer floatBuffer);
    }

    @Environment(EnvType.CLIENT)
    interface Button {
        void onPressButton(ByteBuffer byteBuffer);
    }
}
