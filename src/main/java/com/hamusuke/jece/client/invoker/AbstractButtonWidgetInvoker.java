package com.hamusuke.jece.client.invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface AbstractButtonWidgetInvoker {
    AbstractButtonWidget setOnPressSound(@Nullable SoundEvent soundEvent);

    @Nullable
    SoundEvent getOnPressSound();
}
