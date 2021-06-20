package com.hamusuke.jece.client.joystick;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class JoystickStickyKeybinding extends JoystickKeybinding {
    private final BooleanSupplier toggleGetter;

    public JoystickStickyKeybinding(String translationKey, int code, String category, BooleanSupplier booleanSupplier) {
        super(translationKey, code, category);
        this.toggleGetter = booleanSupplier;
    }

    public void setPressed(boolean pressed) {
        if (this.toggleGetter.getAsBoolean()) {
            if (pressed) {
                super.setPressed(!this.isPressed());
            }
        } else {
            super.setPressed(pressed);
        }
    }
}
