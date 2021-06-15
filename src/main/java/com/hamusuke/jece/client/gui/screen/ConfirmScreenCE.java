package com.hamusuke.jece.client.gui.screen;

import com.google.common.collect.Lists;
import com.hamusuke.jece.invoker.client.ScreenInvoker;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfirmScreenCE extends Screen {
    private final Screen parent;
    private final Text subtitle;
    private static final Text ok = new TranslatableText("gui.ok");
    private final BooleanConsumer booleanConsumer;
    private List<OrderedText> line = Lists.newArrayList();
    private int sizeX;
    private int sizeY;
    private int dialogX;
    private int dialogY;

    public ConfirmScreenCE(Screen parent, Text title, Text subtitle, BooleanConsumer booleanConsumer) {
        super(title);
        this.parent = parent;
        this.subtitle = subtitle;
        this.booleanConsumer = booleanConsumer;
    }

    protected void init() {
        super.init();
        this.parent.init(this.client, this.width, this.height);
        int i = this.textRenderer.fontHeight;
        this.sizeX = this.width / 3 + 16;
        this.line = this.textRenderer.wrapLines(this.subtitle, this.sizeX - 16);
        this.sizeY = 8 + i + 8 + this.line.size() * i + 8 + 20 + 2 + 20 + 5;
        this.dialogX = (this.width - this.sizeX) / 2;
        this.dialogY = (this.height - this.sizeY) / 2;
        int y = this.addButton(new ButtonWidget(this.dialogX + 8, this.dialogY + this.sizeY - 5 - 20, this.sizeX - 16, 20, ok, (button) -> this.booleanConsumer.accept(true))).y;
        this.addButton(new ButtonWidget(this.dialogX + 8, y - 2 - 20, this.sizeX - 16, 20, ScreenTexts.CANCEL, (button) -> this.booleanConsumer.accept(false)));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.parent.render(matrices, -1, -1, delta);
        ((ScreenInvoker) this).renderDialogWindow(matrices, this.dialogX, this.dialogY, this.sizeX, this.sizeY);
        this.textRenderer.draw(matrices, this.title, this.dialogX + 8, this.dialogY + 8, 5592405);
        for (int i = 0; i < this.line.size(); i++) {
            this.textRenderer.draw(matrices, this.line.get(i), this.dialogX + 8, this.dialogY + 8 + 8 + 8 + i * this.textRenderer.fontHeight, 5592405);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.booleanConsumer.accept(false);
    }
}
