package com.hamusuke.jece.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JECEServerOptions {
    private final File configFilePath;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();

    public int autoSaveTicks = 7200;

    public JECEServerOptions(File configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void write() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.configFilePath);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(outputStreamWriter)
        ) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();

            jsonWriter.name("autosaveticks").value(this.autoSaveTicks);

            jsonWriter.endObject();
        } catch (Throwable e) {
            LOGGER.warn("Failed to save options", e);
        }
    }

    public void read() {
        if (this.configFilePath.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(this.configFilePath);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            ) {
                JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);

                if (jsonObject.has("autosaveticks")) {
                    this.autoSaveTicks = jsonObject.get("autosaveticks").getAsInt();
                    if (this.autoSaveTicks >= 0 && this.autoSaveTicks < 900) {
                        this.autoSaveTicks = 900;
                    }
                }
            } catch (Throwable e) {
                LOGGER.warn("Failed to load options", e);
            }
        }
    }
}
