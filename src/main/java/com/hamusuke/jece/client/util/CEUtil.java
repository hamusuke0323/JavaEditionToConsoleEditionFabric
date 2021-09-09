package com.hamusuke.jece.client.util;

import com.hamusuke.jece.JECE;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class CEUtil {
    public static final TextColor SKY_BLUE = TextColor.fromRgb(10329599);
    public static final Identifier DIALOG_WINDOW = new Identifier(JECE.MOD_ID, "textures/gui/dialog.png");
    public static final CubeMapRenderer PANORAMA_RESOURCES_CE = new CubeMapRenderer(new Identifier(JECE.MOD_ID, "textures/gui/title/background/panorama"));
    public static final Identifier PANORAMA_OVERLAY_CE = new Identifier(JECE.MOD_ID, "textures/gui/title/background/panorama_overlay.png");

    public static Dimension getScaledDimensionMaxRatio(Dimension imageSize, Dimension boundary) {
        double ratio = Math.max(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }

    public static Dimension getScaledDimensionMinRatio(Dimension imageSize, Dimension boundary) {
        double ratio = Math.min(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }

    public static boolean cannotRenderHotbars(MinecraftClient client) {
        return client.currentScreen instanceof HandledScreen;
    }
}
