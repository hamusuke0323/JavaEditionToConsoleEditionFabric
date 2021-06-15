package com.hamusuke.jece.client.gui.screen;

import com.google.common.collect.Lists;
import com.hamusuke.jece.invoker.client.ScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class SaveScreen extends Screen {
    private final Screen parent;
    private final Consumer<ButtonWidget> saveAndReturnToMenu;
    private final Consumer<ButtonWidget> withoutSaveAndReturnToMenu;
    private final Consumer<ButtonWidget> cancel;
    private List<OrderedText> lines = Lists.newArrayList();
    private int sizeX;
    private int sizeY;
    private int dialogX;
    private int dialogY;

    public SaveScreen(Screen parent, Consumer<ButtonWidget> saveAndReturnToMenu, Consumer<ButtonWidget> withoutSaveAndReturnToMenu, Consumer<ButtonWidget> cancel) {
        super(new TranslatableText("menu.quit"));
        this.parent = parent;
        this.saveAndReturnToMenu = saveAndReturnToMenu;
        this.withoutSaveAndReturnToMenu = withoutSaveAndReturnToMenu;
        this.cancel = cancel;
    }

    protected void init() {
        super.init();
        this.parent.init(this.client, this.width, this.height);
        int i = this.textRenderer.fontHeight;
        this.sizeX = this.width / 3 + 16;
        this.lines = this.textRenderer.wrapLines(new TranslatableText("menu.quit.finally"), this.sizeX - 16);
        this.sizeY = 8 + i + 8 + this.lines.size() * i + 8 + 20 + 2 + 20 + 2 + 20 + 10;
        this.dialogX = (this.width - this.sizeX) / 2;
        this.dialogY = (this.height - this.sizeY) / 2;
        int y = this.addButton(new ButtonWidget(this.dialogX + 8, this.dialogY + this.sizeY - 5 - 20, this.sizeX - 16, 20, new TranslatableText("gamemenu.withoutsaveandexit"), (b) -> {
            this.client.openScreen(new ConfirmScreenCE(this, new TranslatableText("gamemenu.withoutsaveandexit"), new TranslatableText("gamemenu.withoutsaveandexit.desc"), (bool) -> {
                if (bool) {
                    this.client.openScreen(this.parent);
                    this.withoutSaveAndReturnToMenu.accept(b);
                } else {
                    this.client.openScreen(this);
                }
            }));
        })).y;
        y = this.addButton(new ButtonWidget(this.dialogX + 8, y - 2 - 20, this.sizeX - 16, 20, new TranslatableText("gamemenu.saveandexit"), (b) -> {
            this.client.openScreen(new ConfirmScreenCE(this, new TranslatableText("menu.game.save"), new TranslatableText("menu.game.save.desc"), (bool) -> {
                if (bool) {
                    this.client.openScreen(this.parent);
                    this.saveAndReturnToMenu.accept(b);
                } else {
                    this.client.openScreen(this);
                }
            }));
        })).y;
        this.addButton(new ButtonWidget(this.dialogX + 8, y - 2 - 20, this.sizeX - 16, 20, ScreenTexts.CANCEL, this.cancel::accept));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.parent.render(matrices, -1, -1, delta);
        if (this.client.currentScreen != this) {
            return;
        }
        ((ScreenInvoker) this).renderDialogWindow(matrices, this.dialogX, this.dialogY, this.sizeX, this.sizeY);
        this.textRenderer.draw(matrices, this.title, this.dialogX + 8, this.dialogY + 8, 5592405);
        for (int i = 0; i < this.lines.size(); i++) {
            this.textRenderer.draw(matrices, this.lines.get(i), this.dialogX + 8, this.dialogY + 8 + 8 + 8 + i * this.textRenderer.fontHeight, 5592405);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
