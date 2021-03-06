package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.gui.screen.ConfirmScreenCE;
import com.hamusuke.jece.client.gui.screen.HowToPlayAndOptionsScreen;
import com.hamusuke.jece.client.gui.screen.SaveScreen;
import com.hamusuke.jece.invoker.MinecraftServerInvoker;
import com.hamusuke.jece.invoker.client.AdvancementsScreenInvoker;
import com.hamusuke.jece.invoker.client.ClickableWidgetInvoker;
import com.hamusuke.jece.invoker.client.ScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
@Environment(EnvType.CLIENT)
public abstract class GameMenuScreenMixin extends Screen {
    @Shadow
    @Final
    private boolean showMenu;

    private GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    protected void init(CallbackInfo ci) {
        if (this.showMenu) {
            this.addButton(((ClickableWidgetInvoker) new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + -16, 204, 20, new TranslatableText("menu.returnToGame"), (buttonWidget) -> {
                this.client.openScreen(null);
                this.client.mouse.lockCursor();
            })).setOnPressSound(JECEClient.UI_BACKBUTTON_CLICK));

            this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + 24 + -16, 204, 20, new TranslatableText("menu.options"), (p_213071_1_) -> {
                this.client.openScreen(new HowToPlayAndOptionsScreen(this));
            }));

            this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + 24 + 24 + -16, 204, 20, new TranslatableText("gui.advancements"), (buttonWidget) -> {
                this.client.openScreen(((AdvancementsScreenInvoker) new AdvancementsScreen(this.client.player.networkHandler.getAdvancementHandler())).setParentScreen(this));
            }));

            if (this.client.isInSingleplayer()) {
                this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + (48 + 24 + 24 + 24) + -16, 204, 20, new TranslatableText("menu.game.save"), (p_213066_1_) -> {
                    IntegratedServer integratedServer = this.client.getServer();
                    if (integratedServer != null) {
                        this.client.openScreen(new ConfirmScreenCE(this, new TranslatableText("menu.game.save"), new TranslatableText("menu.game.save.desc"), (bool) -> {
                            if (bool) {
                                integratedServer.execute(() -> {
                                    ((MinecraftServerInvoker) integratedServer).saveAll();
                                    this.client.send(() -> this.client.openScreen(this));
                                });
                            }

                            this.client.openScreen(this);
                        }));
                    } else {
                        this.client.openScreen(this);
                    }
                }));
            }

            ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 48 + 72 + (this.client.isInSingleplayer() ? 24 : 0) + -16, 204, 20, new TranslatableText("menu.returnToMenu"), (buttonWidget) -> {
                boolean bl = this.client.isInSingleplayer();
                boolean bl2 = this.client.isConnectedToRealms();
                if (bl) {
                    this.client.openScreen(new SaveScreen((GameMenuScreen) (Object) this, (button) -> {
                        buttonWidget.active = false;
                        button.active = false;
                        this.exitClient();
                    }, (button) -> {
                        buttonWidget.active = false;
                        button.active = false;
                        this.exitClient();
                    }, (button) -> {
                        button.active = false;
                        this.client.openScreen((GameMenuScreen) (Object) this);
                    }));
                } else {
                    buttonWidget.active = false;
                    this.client.world.disconnect();
                    this.client.disconnect();
                }

                if (bl2) {
                    RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
                    realmsBridgeScreen.switchToRealms(new TitleScreen());
                } else if (!bl) {
                    this.client.openScreen(new MultiplayerScreen(new TitleScreen()));
                }
            }));
            if (!this.client.isInSingleplayer()) {
                buttonWidget2.setMessage(new TranslatableText("menu.disconnect"));
            }
        }
        ci.cancel();
    }

    private void exitClient() {
        this.client.world.disconnect();
        this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
        this.client.openScreen(new TitleScreen());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.showMenu) {
            ((ScreenInvoker) this).renderMinecraftTitle(matrices, true);
        } else {
            drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 16777215);
        }

        if (this.client.currentScreen != this) {
            ci.cancel();
            return;
        }

        super.render(matrices, mouseX, mouseY, delta);
        ci.cancel();
    }
}
