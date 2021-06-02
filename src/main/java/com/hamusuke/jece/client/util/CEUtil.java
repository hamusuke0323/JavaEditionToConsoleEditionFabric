package com.hamusuke.jece.client.util;

import com.hamusuke.jece.client.MainClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CEUtil {
    public static final TextColor SKY_BLUE = TextColor.fromRgb(10329599);

    public static final Identifier DIALOG_WINDOW = new Identifier(MainClient.MOD_ID, "textures/gui/dialog.png");
}
