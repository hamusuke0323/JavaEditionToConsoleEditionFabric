package com.hamusuke.jece.client.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.input.Input;

@Environment(EnvType.CLIENT)
public interface KeyboardInputTickEvent {
    Event<KeyboardInputTickEvent> EVENT = EventFactory.createArrayBacked(KeyboardInputTickEvent.class, (listener) -> (input, slowDown) -> {
        for (KeyboardInputTickEvent event : listener) {
            event.onTick(input, slowDown);
        }
    });

    void onTick(Input input, boolean slowDown);
}
