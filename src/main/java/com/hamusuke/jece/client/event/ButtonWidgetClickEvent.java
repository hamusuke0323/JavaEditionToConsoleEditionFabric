package com.hamusuke.jece.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Arrays;

public interface ButtonWidgetClickEvent {
    Event<ButtonWidgetClickEvent> EVENT = EventFactory.createArrayBacked(ButtonWidgetClickEvent.class, (listeners) -> (mouseX, mouseY) -> Arrays.stream(listeners).forEach(e -> e.onClick(mouseX, mouseY)));

    void onClick(double mouseX, double mouseY);
}
