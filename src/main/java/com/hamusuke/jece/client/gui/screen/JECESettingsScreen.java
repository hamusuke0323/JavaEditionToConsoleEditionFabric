package com.hamusuke.jece.client.gui.screen;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.options.JECEOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class JECESettingsScreen extends Screen {
    private static final Option[] OPTIONS = new Option[]{JECEOption.DISPLAY_PLAYER_ON_SCREEN, JECEOption.AUTO_SAVE_TICKS};
    @Nullable
    private final Screen parent;
    private ButtonListWidget buttonListWidget;

    public JECESettingsScreen(@Nullable Screen parent) {
        super(new TranslatableText("jece.settings.screen"));
        this.parent = parent;
    }

    protected void init() {
        super.init();
        this.buttonListWidget = new ButtonListWidget(this.client, this.width, this.height, 20, this.height - 20, 25);
        this.buttonListWidget.addAll(OPTIONS);
        this.buttonListWidget.addSingleOptionEntry(new Option("jece.switcher.screen") {
            public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
                return new ButtonWidget(x, y, width, 20, this.getDisplayPrefix(), (button) -> JECESettingsScreen.this.client.openScreen(new JECESwitcherScreen(JECESettingsScreen.this)));
            }
        });
        this.children.add(this.buttonListWidget);
        this.addButton(new ButtonWidget(this.width / 4, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, (button) -> this.onClose()));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.buttonListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 6, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void removed() {
        JECEClient.jeceOptions.write();
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }
}
