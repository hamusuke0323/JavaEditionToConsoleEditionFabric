package com.hamusuke.jece.invoker.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public interface ItemInvoker {
    UUID ATTACK_DAMAGE_MODIFIER_ID();

    UUID ATTACK_SPEED_MODIFIER_ID();
}
