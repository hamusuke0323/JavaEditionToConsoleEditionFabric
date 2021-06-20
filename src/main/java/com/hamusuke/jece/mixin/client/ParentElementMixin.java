package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.gui.JoystickElement;
import com.hamusuke.jece.client.gui.JoystickParentElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParentElement.class)
@Environment(EnvType.CLIENT)
public interface ParentElementMixin extends JoystickParentElement {
    @Shadow
    @Nullable Element getFocused();

    default boolean joystickButtonPressed(int key) {
        return this.getFocused() != null && ((JoystickElement) this.getFocused()).joystickButtonPressed(key);
    }

    default boolean joystickLStickMoved(double x, double y) {
        return this.getFocused() != null && ((JoystickElement) this.getFocused()).joystickLStickMoved(x, y);
    }

    default boolean joystickRStickMoved(double x, double y) {
        return this.getFocused() != null && ((JoystickElement) this.getFocused()).joystickRStickMoved(x, y);
    }
}
