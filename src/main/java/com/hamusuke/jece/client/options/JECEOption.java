package com.hamusuke.jece.client.options;

import com.hamusuke.jece.client.JECEClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class JECEOption {
    public static final BooleanOption DISPLAY_PLAYER_ON_SCREEN = new BooleanOption("options.displayplayeronscreen", (gameOptions) -> JECEClient.jeceOptions.displayPlayerOnScreen, (gameOptions, bool) -> {
        JECEClient.jeceOptions.displayPlayerOnScreen = bool;
        JECEClient.jeceOptions.write();
    });
    public static final CyclingOption AUTO_SAVE_TICKS = new CyclingOption("options.autosaveticks", (gameOptions, amount) -> {
        if (JECEClient.jeceOptions.autoSaveTicks < 0) {
            JECEClient.jeceOptions.autoSaveTicks = 900;
            JECEClient.jeceOptions.write();
            return;
        } else if (JECEClient.jeceOptions.autoSaveTicks >= 28800) {
            JECEClient.jeceOptions.autoSaveTicks = -1;
            JECEClient.jeceOptions.write();
            return;
        }

        JECEClient.jeceOptions.autoSaveTicks *= 2;
        JECEClient.jeceOptions.write();
    }, (gameOptions, cyclingOption) -> {
        cyclingOption.setTooltip(MinecraftClient.getInstance().textRenderer.wrapLines(new TranslatableText("options.autosaveticks.desc"), 200));

        int l = 900;
        Text text = new TranslatableText("options.autosaveticks");

        if (JECEClient.jeceOptions.autoSaveTicks < 0) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.none"));
        } else if (JECEClient.jeceOptions.autoSaveTicks <= l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.45s"));
        } else if (JECEClient.jeceOptions.autoSaveTicks <= 2 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.90s"));
        } else if (JECEClient.jeceOptions.autoSaveTicks <= 4 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.3min"));
        } else if (JECEClient.jeceOptions.autoSaveTicks <= 8 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.6min"));
        } else {
            return JECEClient.jeceOptions.autoSaveTicks <= 16 * l ? new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.12min")) : new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.24min"));
        }
    });
}
