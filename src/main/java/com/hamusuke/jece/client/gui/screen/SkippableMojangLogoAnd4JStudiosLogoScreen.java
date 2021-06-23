package com.hamusuke.jece.client.gui.screen;

import com.hamusuke.jece.JECE;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkippableMojangLogoAnd4JStudiosLogoScreen extends Screen {
    private final TitleScreen menu;
    private static final Identifier MOJANG = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/mojang.png");
    private static final Identifier FourJSTUDIOS = new Identifier(JECE.MOD_ID, "textures/gui/title/startupframes/4jstudios.png");
    private static final float IncOrDecSize = 0.02F;
    private static final int waitSize = 160;
    private float fade = 0.0F;
    private int timer = 0;
    private boolean showedMojangLogo;
    private boolean fadeout;

    public SkippableMojangLogoAnd4JStudiosLogoScreen(TitleScreen menu) {
        super(LiteralText.EMPTY);
        this.menu = menu;
    }

    public void render(MatrixStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        float f = MathHelper.clamp(this.fade, 0.0F, 1.0F);

        if (!this.showedMojangLogo) {
            RenderSystem.color4f(f, f, f, f);
            this.client.getTextureManager().bindTexture(MOJANG);
            drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 1920, 1080, 1920, 1080);
            if (this.fade >= 1.0F && this.timer >= waitSize) {
                this.showedMojangLogo = true;
                this.fadeout = false;
                this.fade = 0.0F;
                this.timer = 0;
            } else if (this.fade >= 1.0F) {
                this.timer++;
            } else {
                this.fade += IncOrDecSize;
            }
        } else {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, f);
            this.client.getTextureManager().bindTexture(FourJSTUDIOS);
            drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 1920, 1080, 1920, 1080);

            if (this.fadeout) {
                this.onClose();
            }

            if (this.fade >= 1.0F && this.timer >= waitSize) {
                this.fadeout = true;
            } else if (this.fade >= 1.0F) {
                this.timer++;
            } else {
                this.fade += IncOrDecSize;
            }
        }
        RenderSystem.popMatrix();
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        this.client.openScreen(this.menu);
    }

    public boolean joystickButtonPressed(int key) {
        this.onClose();
        return true;
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        this.onClose();
        return true;
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        this.onClose();
        return true;
    }
}
