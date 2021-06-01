package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.invoker.ItemInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Item.class)
@Environment(EnvType.CLIENT)
public class ItemMixin implements ItemInvoker {
    @Shadow
    @Final
    protected static UUID ATTACK_DAMAGE_MODIFIER_ID;

    @Shadow
    @Final
    protected static UUID ATTACK_SPEED_MODIFIER_ID;

    public UUID ATTACK_DAMAGE_MODIFIER_ID() {
        return ATTACK_DAMAGE_MODIFIER_ID;
    }

    public UUID ATTACK_SPEED_MODIFIER_ID() {
        return ATTACK_SPEED_MODIFIER_ID;
    }
}
