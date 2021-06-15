package com.hamusuke.jece.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.options.MouseOptionsScreen;
import net.minecraft.client.gui.screen.options.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class OptionsScreenCE extends Screen {
    private final Screen parent;
    private final GameOptions settings;

    public OptionsScreenCE(Screen parent, GameOptions gameOptions) {
        super(new TranslatableText("options.title"));
        this.parent = parent;
        this.settings = gameOptions;
    }

    protected void init() {
        super.init();
        this.parent.init(this.client, this.width, this.height);

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 204, 20, new TranslatableText("menu.gameoption"), (p_213070_1_) -> this.client.openScreen(new MouseOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24) + -16, 204, 20, new TranslatableText("menu.audio"), (p_213071_1_) -> this.client.openScreen(new SoundOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24 + 24) + -16, 204, 20, new TranslatableText("menu.graphic"), (p_213065_1_) -> this.client.openScreen(new VideoOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24 + 24 + 24) + -16, 204, 20, new TranslatableText("menu.userinterface"), (p_213066_1_) -> this.client.openScreen(new AccessibilityOptionsScreen(this, this.client.options))));

        this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 72 + 24) + -16, 204, 20, new TranslatableText("menu.settings.reset"), (p_213067_1_) -> {
            this.client.openScreen(new ConfirmScreenCE(this, new TranslatableText("menu.settings.reset"), new TranslatableText("menu.settings.reset.desc"), (bool) -> {
                if (bool) {
                    //this.client.options.();
                }

                this.client.openScreen(this);
            }));
        }));
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

    public void removed() {
        this.settings.write();
    }
}
