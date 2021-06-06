package com.hamusuke.jece.client.jececomparator;

import com.hamusuke.jece.client.util.CEUtil;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class JECEComparator {
    private final Identifier illustration;
    private final int textureWidth;
    private final int textureHeight;
    private final Text title;
    private final Text description;
    private final Text je = new TranslatableText("jece.switcher.je");
    private final Text ce = new TranslatableText("jece.switcher.ce");
    private final BooleanConsumer onPress;
    private final BooleanGetter booleanGetter;

    public JECEComparator(Identifier illustration, int textureWidth, int textureHeight, Text title, Text description, AtomicBoolean setterGetter) {
        this.illustration = illustration;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.title = title;
        this.description = description;
        this.onPress = setterGetter::set;
        this.booleanGetter = setterGetter::get;
    }

    public void render(MinecraftClient client, MatrixStack matrices, int rowLeft, int rowTop, int rowWidth, int rowHeight) {
        float halfRowWidth = (float) rowWidth / 2;
        float centerX = rowLeft + halfRowWidth;
        int fontHeight = client.textRenderer.fontHeight;
        client.textRenderer.drawWithShadow(matrices, this.title, centerX - (float) client.textRenderer.getWidth(this.title) / 2, rowTop, 16777215);
        client.textRenderer.drawWithShadow(matrices, this.je, (rowLeft + halfRowWidth / 2) - (float) client.textRenderer.getWidth(this.je) / 2, rowTop + fontHeight + 1, 16777215);
        client.textRenderer.drawWithShadow(matrices, this.ce, (centerX + halfRowWidth / 2) - (float) client.textRenderer.getWidth(this.ce) / 2, rowTop + fontHeight + 1, 16777215);
        List<OrderedText> wrappedLines = client.textRenderer.wrapLines(this.description, rowWidth);

        client.getTextureManager().bindTexture(this.illustration);
        Dimension dimension = CEUtil.getScaledDimensionMinRatio(new Dimension(this.textureWidth, this.textureHeight), new Dimension(rowWidth, rowHeight - 20 - wrappedLines.size() * fontHeight - fontHeight * 2 - 2));
        int illustrationY = rowTop + fontHeight * 2 + 2;
        DrawableHelper.drawTexture(matrices, (int) centerX - dimension.width / 2, illustrationY, 0.0F, 0.0F, dimension.width, dimension.height, dimension.width, dimension.height);

        for (int i = 0; i < wrappedLines.size(); i++) {
            client.textRenderer.drawWithShadow(matrices, wrappedLines.get(i), rowLeft, illustrationY + dimension.height + i * fontHeight, 16777215);
        }
    }

    public void onPressUseJE() {
        this.onPress.accept(true);
    }

    public void onPressUseCE() {
        this.onPress.accept(false);
    }

    public boolean isJESelected() {
        return this.booleanGetter.isJESelected();
    }

    @Environment(EnvType.CLIENT)
    public interface BooleanGetter {
        boolean isJESelected();
    }
}
