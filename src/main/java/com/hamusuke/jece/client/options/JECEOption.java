package com.hamusuke.jece.client.options;

import com.hamusuke.jece.client.MainClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class JECEOption {
    public static final BooleanOption DISPLAY_PLAYER_ON_SCREEN = new BooleanOption("options.displayplayeronscreen", (gameOptions) -> MainClient.jeceOptions.displayPlayerOnScreen, (gameOptions, bool) -> {
        MainClient.jeceOptions.displayPlayerOnScreen = bool;
        MainClient.jeceOptions.write();
    });
    public static final CyclingOption AUTO_SAVE_TICKS = new CyclingOption("options.autosaveticks", (gameOptions, amount) -> {
        if (MainClient.jeceOptions.autoSaveTicks < 0) {
            MainClient.jeceOptions.autoSaveTicks = 900;
            return;
        } else if (MainClient.jeceOptions.autoSaveTicks == 28800) {
            MainClient.jeceOptions.autoSaveTicks = -1;
            return;
        }

        MainClient.jeceOptions.autoSaveTicks *= 2;
        MainClient.jeceOptions.write();
    }, (gameOptions, cyclingOption) -> {
        int l = 900;
        Text text = new TranslatableText("options.autosaveticks");

        if (MainClient.jeceOptions.autoSaveTicks < 0) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.none"));
        } else if (MainClient.jeceOptions.autoSaveTicks <= l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.45s"));
        } else if (MainClient.jeceOptions.autoSaveTicks <= 2 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.90s"));
        } else if (MainClient.jeceOptions.autoSaveTicks <= 4 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.3min"));
        } else if (MainClient.jeceOptions.autoSaveTicks <= 8 * l) {
            return new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.6min"));
        } else {
            return MainClient.jeceOptions.autoSaveTicks <= 16 * l ? new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.12min")) : new TranslatableText("options.separator", text, new TranslatableText("options.autosaveticks.24min"));
        }
    });
}
