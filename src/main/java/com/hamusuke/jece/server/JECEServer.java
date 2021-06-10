package com.hamusuke.jece.server;

import com.hamusuke.jece.command.AutoSaveCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class JECEServer implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger();
    public static File jeceServerConfigDir;
    public static JECEServerOptions jeceServerOptions;

    public void onInitializeServer() {
        jeceServerConfigDir = FabricLoader.getInstance().getConfigDir().resolve("jece-server").toFile();
        if (!jeceServerConfigDir.exists() && jeceServerConfigDir.mkdir()) {
            LOGGER.info("jece-server config directory not found. made the directory.");
        }

        jeceServerOptions = new JECEServerOptions(new File(jeceServerConfigDir, "config.json"));
        jeceServerOptions.read();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            if (dedicated) {
                AutoSaveCommand.register(dispatcher);
            }
        });
    }
}
