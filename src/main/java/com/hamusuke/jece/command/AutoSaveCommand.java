package com.hamusuke.jece.command;

import com.hamusuke.jece.server.JECEServer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class AutoSaveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("autosave").requires((source) -> source.hasPermissionLevel(2));
        for (String string : new String[]{"off", "900", "1800", "3600", "7200", "14400", "28800"}) {
            literalArgumentBuilder.then(CommandManager.literal(string).executes((context) -> {
                boolean off = string.equalsIgnoreCase("off");

                if (off) {
                    JECEServer.jeceServerOptions.autoSaveTicks = -1;
                } else {
                    JECEServer.jeceServerOptions.autoSaveTicks = Integer.parseInt(string);
                }

                context.getSource().sendFeedback(new TranslatableText("jece.command.autosave." + string), true);
                JECEServer.jeceServerOptions.write();
                return off ? -1 : Integer.parseInt(string);
            }));
        }
        dispatcher.register(literalArgumentBuilder);
    }
}
