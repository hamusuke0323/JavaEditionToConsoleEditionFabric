package com.hamusuke.jece.client.jececomparator;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.MainClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class JECEComparators {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    public static final List<JECEComparator> JECE_COMPARATORS = Lists.newArrayList();
    public static final JECEComparator TITLE_SCREEN = registerComparator("title_screen", new Identifier(JECE.MOD_ID, "textures/gui/jeceswitcher/titlescreen.png"), 3840, 1080, "jece.switchers.titlescreen", JECEStorage.TITLE_SCREEN_BOOLEAN);
    public static final JECEComparator ENCHANTMENT = registerComparator("enchantment", new Identifier(JECE.MOD_ID, "textures/gui/jeceswitcher/enchantment.png"), 1266, 388, "jece.switchers.enchantment", JECEStorage.ENCHANTMENT_BOOLEAN);

    private static JECEComparator registerComparator(String id, Identifier illustration, int textureWidth, int textureHeight, String translationKey, AtomicBoolean setterGetter) {
        checkException(id);
        JECEComparator jeceComparator = new JECEComparator(id, illustration, textureWidth, textureHeight, new TranslatableText(translationKey), new TranslatableText(translationKey + ".desc"), setterGetter);
        JECE_COMPARATORS.add(jeceComparator);
        return jeceComparator;
    }

    private static void checkException(String id) {
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be empty!");
        } else {
            JECE_COMPARATORS.forEach((jeceComparator) -> {
                if (jeceComparator.getId().equals(id)) {
                    throw new IllegalArgumentException("duplicate id: " + id);
                }
            });
        }
    }

    public static synchronized void write() {
        File json = new File(MainClient.jeceConfigDir, "switcher_config.json");
        try (FileOutputStream fileOutputStream = new FileOutputStream(json);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(outputStreamWriter)
        ) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject();
            for (JECEComparator jeceComparator : JECE_COMPARATORS) {
                jsonWriter.name(jeceComparator.getId()).value(jeceComparator.isJESelected());
            }
            jsonWriter.endObject();
        } catch (Exception e) {
            LOGGER.warn("Error occurred while writing config file", e);
        }
    }

    public static synchronized void read() {
        File json = new File(MainClient.jeceConfigDir, "switcher_config.json");
        if (json.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(json);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            ) {
                JsonObject jsonObject = GSON.fromJson(inputStreamReader, JsonObject.class);
                for (JECEComparator jeceComparator : JECE_COMPARATORS) {
                    if (jsonObject.has(jeceComparator.getId())) {
                        jeceComparator.set(jsonObject.get(jeceComparator.getId()).getAsBoolean());
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Error occurred while reading config file", e);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class JECEStorage {
        public static final AtomicBoolean TITLE_SCREEN_BOOLEAN = new AtomicBoolean();
        public static final AtomicBoolean ENCHANTMENT_BOOLEAN = new AtomicBoolean();
    }
}
