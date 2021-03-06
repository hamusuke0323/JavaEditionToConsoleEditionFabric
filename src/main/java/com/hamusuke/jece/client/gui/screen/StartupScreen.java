package com.hamusuke.jece.client.gui.screen;

import com.hamusuke.jece.JECE;
import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.invoker.client.MinecraftClientInvoker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReload;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class StartupScreen extends Screen {
    private final MinecraftClient mc;
    private final ResourceReload reload;
    private final Consumer<Optional<Throwable>> exceptionHandler;
    private float progress;
    private float fadeout;
    private static final Identifier TITLE_IMAGE = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/title.png");
    private static final Identifier MOJANG = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/mojang.png");
    private static final Identifier FourJSTUDIOS = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/4jstudios.png");
    private int counter = 1;

    public StartupScreen(MinecraftClient mc, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler) {
        super(LiteralText.EMPTY);
        this.mc = mc;
        this.reload = monitor;
        this.exceptionHandler = exceptionHandler;
    }

    public static void loadStartupTextures(MinecraftClient mc) {
        for (int i = 1; i <= 96; i++) {
            Identifier location = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/" + i + ".png");
            mc.getTextureManager().registerTexture(location, new StartupTextures(location));
        }
        mc.getTextureManager().registerTexture(TITLE_IMAGE, new StartupTextures(TITLE_IMAGE));
        mc.getTextureManager().registerTexture(MOJANG, new StartupTextures(MOJANG));
        mc.getTextureManager().registerTexture(FourJSTUDIOS, new StartupTextures(FourJSTUDIOS));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.pushMatrix();
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TITLE_IMAGE);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 1920, 1080, 1920, 1080);
        if (this.counter <= 96) {
            this.mc.getTextureManager().bindTexture(new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/" + MathHelper.clamp(this.counter, 1, 96) + ".png"));
            drawTexture(matrices, 0, 0, this.width / 6 + 15, this.height / 6 + 3, 0.0F, 0.0F, 382, 192, 382, 192);
            this.counter++;
        }
        RenderSystem.popMatrix();

        super.render(matrices, mouseX, mouseY, delta);

        float f3 = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);

        if (this.progress >= 0.95F) {
            if (((MinecraftClientInvoker) this.mc).getPlayer() != null) {
                ((MinecraftClientInvoker) this.mc).getPlayer().setVolume(this.fadeout);
            }
            this.fadeout -= 0.5F;
        }

        if (this.reload.isComplete()) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.exceptionHandler.accept(Optional.of(throwable));
            }

            if (((MinecraftClientInvoker) this.mc).getPlayer() != null) {
                ((MinecraftClientInvoker) this.mc).getPlayer().stop();
            }
            JECEClient.isFirst = false;
            if (!this.mc.getMusicTracker().isPlayingType(MusicType.MENU)) {
                this.mc.getMusicTracker().play(MusicType.MENU);
            }
            TitleScreen titleScreen = new TitleScreen();
            titleScreen.init(this.mc, this.width, this.height);
            this.mc.openScreen(new SkippableMojangLogoAnd4JStudiosLogoScreen(titleScreen));
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    static class StartupTextures extends ResourceTexture {
        private final Identifier location;

        public StartupTextures(Identifier location) {
            super(location);
            this.location = location;
        }

        protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
            try (InputStream inputstream = DefaultResourcePack.class.getResourceAsStream("/assets/" + this.location.getNamespace() + "/" + this.location.getPath())) {
                if (inputstream == null) {
                    throw new IOException(this.location + " is null");
                }

                return new TextureData(null, NativeImage.read(inputstream));
            } catch (IOException ioexception1) {
                return new ResourceTexture.TextureData(ioexception1);
            }
        }
    }
}
