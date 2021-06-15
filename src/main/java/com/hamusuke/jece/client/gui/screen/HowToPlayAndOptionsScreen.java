package com.hamusuke.jece.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.hamusuke.jece.invoker.client.CreditsScreenInvoker;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class HowToPlayAndOptionsScreen extends Screen {
    private final Screen parent;

    public HowToPlayAndOptionsScreen(Screen parent) {
        super(LiteralText.EMPTY);
        this.parent = parent;
    }

    protected void init() {
        super.init();
        this.parent.init(this.client, this.width, this.height);

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 204, 20, new TranslatableText("options.skinCustomisation"), (p_213070_1_) -> this.client.openScreen(new SkinOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24) + -16, 204, 20, new TranslatableText("menu.settings.howtoplay"), (p_213071_1_) -> {
            //this.client.openScreen(new ChooseHowToPlayScreen(this));
        }));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24 + 24) + -16, 204, 20, new TranslatableText("options.controls"), (p_213065_1_) -> this.client.openScreen(new ControlsOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24 + 24 + 24) + -16, 204, 20, new TranslatableText("menu.settings"), (p_213066_1_) -> this.client.openScreen(new OptionsScreenCE(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 72 + 24) + -16, 204, 20, new TranslatableText("menu.credits"), (p_213067_1_) -> this.client.openScreen(((CreditsScreenInvoker) new CreditsScreen(false, Runnables.doNothing())).setParentScreen(this))));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.parent.render(matrices, -1, -1, delta);

        if (this.client.currentScreen != this) {
            return;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
