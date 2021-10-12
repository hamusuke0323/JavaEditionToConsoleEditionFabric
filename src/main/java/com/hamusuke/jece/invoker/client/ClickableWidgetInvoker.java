package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ClickableWidgetInvoker {
    ClickableWidget setOnPressSound(@Nullable SoundEvent soundEvent);

    @Nullable
    SoundEvent getOnPressSound();
}
