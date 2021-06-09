package com.hamusuke.jece.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class JECESettingsScreen extends Screen {
    @Nullable
    private final Screen parent;

    public JECESettingsScreen(@Nullable Screen parent) {
        super(LiteralText.EMPTY);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if (this.parent != null) {
            this.parent.init(this.client, this.width, this.height);
        }
    }



    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }
}
