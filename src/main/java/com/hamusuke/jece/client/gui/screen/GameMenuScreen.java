package com.hamusuke.jece.client.gui.screen;

import com.hamusuke.jece.client.invoker.ScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class GameMenuScreen extends Screen {
    private final boolean showMenu;

    public GameMenuScreen(boolean showMenu) {
        super(showMenu ? new TranslatableText("menu.game") : new TranslatableText("menu.paused"));
        this.showMenu = showMenu;
    }

    protected void init() {
        if (this.showMenu) {
            this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 204, 20, new TranslatableText("menu.returnToGame"), (buttonWidget) -> {
                this.client.openScreen(null);
                this.client.mouse.lockCursor();
            }));

            this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + 24 + -16, 204, 20, new TranslatableText("menu.options"), (p_213071_1_) -> {
                this.client.openScreen(new HowToPlayAndOptionsScreen(this));
            }));

            this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + 24 + 24 + -16, 204, 20, new TranslatableText("gui.advancements"), (buttonWidget) -> {
                this.client.openScreen(new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler()));
            }));

            ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, new TranslatableText("menu.returnToMenu"), (buttonWidgetx) -> {
                boolean bl = this.client.isInSingleplayer();
                boolean bl2 = this.client.isConnectedToRealms();
                buttonWidgetx.active = false;
                this.client.world.disconnect();
                if (bl) {
                    this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
                } else {
                    this.client.disconnect();
                }

                if (bl) {
                    this.client.openScreen(new TitleScreen());
                } else if (bl2) {
                    RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
                    realmsBridgeScreen.switchToRealms(new TitleScreen());
                } else {
                    this.client.openScreen(new MultiplayerScreen(new TitleScreen()));
                }

            }));
            if (!this.client.isInSingleplayer()) {
                buttonWidget2.setMessage(new TranslatableText("menu.disconnect"));
            }
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.showMenu) {
            ((ScreenInvoker) this).renderMinecraftTitle(matrices, true);
        } else {
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 16777215);
        }

        if (this.client.currentScreen != this) {
            return;
        }

        super.render(matrices, mouseX, mouseY, delta);
    }
}
