package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.gui.JoystickElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Element.class)
@Environment(EnvType.CLIENT)
public interface ElementMixin extends JoystickElement {
    default boolean joystickButtonPressed(int key) {
        return false;
    }

    default boolean joystickLStickMoved(double x, double y) {
        return false;
    }

    default boolean joystickRStickMoved(double x, double y) {
        return false;
    }
}
