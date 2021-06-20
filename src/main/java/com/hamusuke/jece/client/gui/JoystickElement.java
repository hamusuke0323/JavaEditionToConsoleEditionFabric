package com.hamusuke.jece.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface JoystickElement {
    boolean joystickButtonPressed(int key);

    boolean joystickLStickMoved(double x, double y);

    boolean joystickRStickMoved(double x, double y);
}
