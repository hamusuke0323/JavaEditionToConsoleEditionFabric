package com.hamusuke.jece.client.defaultcomparator;

import com.hamusuke.jece.client.util.CEUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

public class DefaultComparator {
    private final Identifier illustration;
    private final int textureWidth;
    private final int textureHeight;
    private final Text title;
    private final Text description;
    private final Runnable onPressUseDefault;
    private final Runnable onPressUseConsoleEdition;
    private final DefaultBoolean defaultBoolean;

    public DefaultComparator(Identifier illustration, int textureWidth, int textureHeight, Text title, Text description, Runnable onPressUseDefault, Runnable onPressUseConsoleEdition, DefaultBoolean defaultBoolean) {
        this.illustration = illustration;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.title = title;
        this.description = description;
        this.onPressUseDefault = onPressUseDefault;
        this.onPressUseConsoleEdition = onPressUseConsoleEdition;
        this.defaultBoolean = defaultBoolean;
    }

    public void render(MinecraftClient client, MatrixStack matrices, int rowLeft, int rowTop, int rowWidth, int rowHeight) {
        client.textRenderer.drawWithShadow(matrices, this.title, (float) ((rowLeft + rowWidth / 2) - client.textRenderer.getWidth(this.title) / 2), rowTop, 16777215);
        /*
        List<OrderedText> wrappedLines = client.textRenderer.wrapLines(this.description, rowWidth);

        client.getTextureManager().bindTexture(this.illustration);
        Dimension dimension = CEUtil.getScaledDimensionMinRatio(new Dimension(this.textureWidth, this.textureHeight), new Dimension(rowWidth, rowHeight - 20 - wrappedLines.size() * client.textRenderer.fontHeight));
        DrawableHelper.drawTexture(matrices, (rowLeft + rowWidth / 2) - dimension.width, rowTop, 0.0F, 0.0F, dimension.width, dimension.height, dimension.width, dimension.height);

        for (int i = 0; i < wrappedLines.size(); i++) {
            client.textRenderer.drawWithShadow(matrices, wrappedLines.get(i), rowLeft, rowTop + dimension.height + i * client.textRenderer.fontHeight, 16777215);
        }
        */
    }

    public void onPressUseDefault() {
        this.onPressUseDefault.run();
    }

    public void onPressUseConsoleEdition() {
        this.onPressUseConsoleEdition.run();
    }

    public boolean isDefaultSelected() {
        return this.defaultBoolean.isDefault();
    }

    public interface DefaultBoolean {
        boolean isDefault();
    }
}
