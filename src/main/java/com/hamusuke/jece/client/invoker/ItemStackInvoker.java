package com.hamusuke.jece.client.invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface ItemStackInvoker {
    List<Text> getHandedTooltip(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext);
}
