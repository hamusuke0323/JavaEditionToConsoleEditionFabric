package com.hamusuke.jece.client.util;

import com.hamusuke.jece.client.MainClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class CEUtil {
    public static final TextColor SKY_BLUE = TextColor.fromRgb(10329599);
    public static final Identifier DIALOG_WINDOW = new Identifier(MainClient.MOD_ID, "textures/gui/dialog.png");
    public static final CubeMapRenderer PANORAMA_RESOURCES_CE = new CubeMapRenderer(new Identifier(MainClient.MOD_ID, "textures/gui/title/background/panorama"));

    public static Dimension getScaledDimensionMaxRatio(Dimension imageSize, Dimension boundary) {
        double ratio = Math.max(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }

    public static Dimension getScaledDimensionMinRatio(Dimension imageSize, Dimension boundary) {
        double ratio = Math.min(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
        return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
    }
}
