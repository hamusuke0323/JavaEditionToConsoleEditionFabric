package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.invoker.client.SlotInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Slot.class)
@Environment(EnvType.CLIENT)
public class SlotMixin implements SlotInvoker {
    private int size;

    public Slot setSize(int size) {
        this.size = size;
        return (Slot) (Object) this;
    }

    public int getSize() {
        return this.size;
    }
}
