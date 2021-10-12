package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.gui.screen.ProgressBarScreen;
import com.hamusuke.jece.client.gui.screen.StartupScreen;
import com.hamusuke.jece.client.joystick.JoystickListener;
import com.hamusuke.jece.client.joystick.JoystickWorker;
import com.hamusuke.jece.client.util.CEUtil;
import com.hamusuke.jece.client.util.StartupSoundPlayer;
import com.hamusuke.jece.invoker.client.MinecraftClientInvoker;
import com.hamusuke.jece.invoker.client.SplashScreenInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.entity.Entity;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements MinecraftClientInvoker {
    @Shadow
    @Nullable
    public Overlay overlay;

    @Shadow
    public abstract void openScreen(@Nullable Screen screen);

    @Shadow
    public abstract void setOverlay(@Nullable Overlay overlay);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public abstract void disconnect(Screen screen);

    @Shadow
    @Final
    private AtomicReference<WorldGenerationProgressTracker> worldGenProgressTracker;
    @Shadow
    @Final
    public WorldRenderer worldRenderer;
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Final
    public GameRenderer gameRenderer;

    @Shadow
    @Nullable
    public abstract Entity getCameraEntity();

    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Final
    public InGameHud inGameHud;
    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    protected abstract boolean method_31321();

    @Shadow
    @Final
    private static Text field_26841;
    @Shadow
    @Nullable
    private TutorialToast field_26843;
    @Shadow
    @Final
    private TutorialManager tutorialManager;
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    protected abstract void openChatScreen(String text);

    @Shadow
    protected abstract void doAttack();

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract void doItemPick();

    @Shadow
    private int itemUseCooldown;

    @Shadow
    @Final
    public Mouse mouse;

    @Shadow
    public abstract ToastManager getToastManager();

    @Shadow
    @Final
    private SoundManager soundManager;
    private RotatingCubeMapRenderer panorama;
    private StartupSoundPlayer startupSoundPlayer;
    private boolean isCreateWorld;

    private MinecraftClientMixin(String string) {
        super(string);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void MinecraftClient(RunArgs args, CallbackInfo ci) {
        GLFW.glfwSetJoystickCallback((jid, event) -> this.execute(() -> {
            if (event == GLFW.GLFW_CONNECTED) {
                String name = GLFW.glfwGetGamepadName(jid);
                if (name != null) {
                    JECEClient.joystickWorker.set(new JoystickWorker(jid));
                    new JoystickListener((MinecraftClient) (Object) this, JECEClient.joystickWorker.get());
                    JECEClient.joystickWorker.get().schedule();
                    this.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("jece.joystick.connected"), new LiteralText(name)));
                }
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                if (JECEClient.joystickWorker.get() != null) {
                    JECEClient.joystickWorker.get().close();
                    JECEClient.joystickWorker.set(null);
                }
                this.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, new TranslatableText("jece.joystick.disconnected"), null));
            }
        }));

        if (JECEClient.isFirst && this.overlay instanceof SplashScreen) {
            SplashScreenInvoker invoker = (SplashScreenInvoker) this.overlay;
            this.setOverlay(null);
            StartupScreen.loadStartupTextures((MinecraftClient) (Object) this);
            InputStream inputStream = DefaultResourcePack.class.getResourceAsStream("/assets/" + JECE.MOD_ID + "/sounds/gamestart.ogg");
            if (inputStream != null) {
                try {
                    this.startupSoundPlayer = new StartupSoundPlayer(inputStream);
                    this.startupSoundPlayer.play();
                } catch (IOException e) {
                    LOGGER.warn("IOException occurred in StartupSoundPlayer", e);
                }
            } else {
                LOGGER.warn("StartupSound not found, return null!");
            }

            this.openScreen(new StartupScreen((MinecraftClient) (Object) this, invoker.getResourceReloadMonitor(), invoker.getExceptionHandler()));
        }
    }

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void openScreen(@Nullable Screen screen, CallbackInfo ci) {
        if (screen instanceof HandledScreen) {
            this.soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @ModifyArg(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), index = 0)
    private Screen startIntegratedServer(Screen screenIn) {
        if (screenIn instanceof LevelLoadingScreen) {
            ProgressBarScreen progressBarScreen;
            if (this.isCreateWorld) {
                progressBarScreen = new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("creating.spawn.area"), this.worldGenProgressTracker.get(), false);
            } else {
                progressBarScreen = new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("loading.spawn.area"), this.worldGenProgressTracker.get(), false);
            }
            return progressBarScreen;
        }

        return screenIn;
    }

    @Inject(method = "disconnect()V", at = @At("HEAD"), cancellable = true)
    private void disconnect(CallbackInfo ci) {
        if (this.isCreateWorld) {
            this.disconnect(new ProgressBarScreen(new TranslatableText("server.initializing"), new TranslatableText("finding.seed")));
        } else {
            this.disconnect(new ProgressBarScreen());
        }

        ci.cancel();
    }

    @Inject(method = "createWorld", at = @At("HEAD"))
    private void createWorldH(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        this.isCreateWorld = true;
    }

    @Inject(method = "createWorld", at = @At("RETURN"))
    private void createWorldR(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
        this.isCreateWorld = false;
    }

    public StartupSoundPlayer getPlayer() {
        return this.startupSoundPlayer;
    }

    public RotatingCubeMapRenderer getPanorama() {
        if (this.panorama == null) {
            this.panorama = new RotatingCubeMapRenderer(CEUtil.PANORAMA_RESOURCES_CE);
        }

        return this.panorama;
    }

    public boolean isCreateWorld() {
        return this.isCreateWorld;
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void handleJoystickInputEvents(CallbackInfo ci) {
        for (; JECEClient.jeceOptions.keyTogglePerspective.wasPressed(); this.worldRenderer.scheduleTerrainUpdate()) {
            Perspective perspective = this.options.getPerspective();
            this.options.setPerspective(this.options.getPerspective().next());
            if (perspective.isFirstPerson() != this.options.getPerspective().isFirstPerson()) {
                this.gameRenderer.onCameraEntitySet(this.options.getPerspective().isFirstPerson() ? this.getCameraEntity() : null);
            }
        }

        while (JECEClient.jeceOptions.keyHotbarLeft.wasPressed()) {
            int i = this.player.inventory.selectedSlot;
            i--;
            i = i < 0 ? 8 : i;

            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
            } else {
                this.player.inventory.selectedSlot = i;
            }
        }

        while (JECEClient.jeceOptions.keyHotbarRight.wasPressed()) {
            int i = this.player.inventory.selectedSlot;
            i++;
            i = i > 8 ? 0 : i;

            if (this.player.isSpectator()) {
                this.inGameHud.getSpectatorHud().selectSlot(i);
            } else {
                this.player.inventory.selectedSlot = i;
            }
        }

        while (JECEClient.jeceOptions.keySocialInteractions.wasPressed()) {
            if (!this.method_31321()) {
                this.player.sendMessage(field_26841, true);
                NarratorManager.INSTANCE.narrate(field_26841.getString());
            } else {
                if (this.field_26843 != null) {
                    this.tutorialManager.method_31364(this.field_26843);
                    this.field_26843 = null;
                }

                this.openScreen(new SocialInteractionsScreen());
            }
        }

        while (JECEClient.jeceOptions.keyInventory.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                this.player.openRidingInventory();
            } else {
                this.tutorialManager.onInventoryOpened();
                this.openScreen(new InventoryScreen(this.player));
            }
        }

        while (JECEClient.jeceOptions.keyDrop.wasPressed()) {
            if (!this.player.isSpectator() && this.player.dropSelectedItem(Screen.hasControlDown())) {
                this.player.swingHand(Hand.MAIN_HAND);
            }
        }

        boolean bl3 = this.options.chatVisibility != ChatVisibility.HIDDEN;
        if (bl3) {
            while (JECEClient.jeceOptions.keyChat.wasPressed()) {
                this.openChatScreen("");
            }
        }

        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isPressed() && !JECEClient.jeceOptions.keyUse.isPressed()) {
                this.interactionManager.stopUsingItem(this.player);
            }

            label120:
            while (true) {
                if (!JECEClient.jeceOptions.keyAttack.wasPressed()) {
                    while (JECEClient.jeceOptions.keyUse.wasPressed()) {
                    }

                    while (true) {
                        if (JECEClient.jeceOptions.keyPickItem.wasPressed()) {
                            continue;
                        }
                        break label120;
                    }
                }
            }
        } else {
            while (JECEClient.jeceOptions.keyAttack.wasPressed()) {
                this.doAttack();
            }

            while (JECEClient.jeceOptions.keyUse.wasPressed()) {
                this.doItemUse();
            }

            while (JECEClient.jeceOptions.keyPickItem.wasPressed()) {
                this.doItemPick();
            }
        }

        if (JECEClient.jeceOptions.keyUse.isPressed() && this.itemUseCooldown == 0 && !this.player.isUsingItem()) {
            this.doItemUse();
        }
    }

    @ModifyArg(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"), index = 0)
    private boolean modifyHandleBlockBreaking(boolean bl) {
        return this.currentScreen == null && (this.options.keyAttack.isPressed() || JECEClient.jeceOptions.keyAttack.isPressed()) && this.mouse.isCursorLocked();
    }
}
