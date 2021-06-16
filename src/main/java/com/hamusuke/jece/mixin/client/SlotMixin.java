package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.invoker.client.SlotInvoker;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Slot.class)
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
