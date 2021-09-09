package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.util.CEUtil;
import com.hamusuke.jece.invoker.client.ItemStackInvoker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Shadow
    public abstract TextRenderer getFontRenderer();

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void renderHeldItemTooltip(MatrixStack matrices, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
            return;
        }

        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            List<Text> tooltips = ((ItemStackInvoker) (Object) this.currentStack).getHandedTooltip(this.client.player, this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            for (int index = 0; index < tooltips.size(); index++) {
                Text tooltip = tooltips.get(index);

                int i = this.getFontRenderer().getWidth(tooltip);
                int j = (this.scaledWidth - i) / 2;
                int k = this.scaledHeight - 59 - tooltips.size() * this.client.textRenderer.fontHeight + index * this.client.textRenderer.fontHeight;
                if (!this.client.interactionManager.hasStatusBars()) {
                    k += 14;
                }

                int l = (int) ((float) this.heldItemTooltipFade * 256.0F / 10.0F);
                if (l > 255) {
                    l = 255;
                }

                if (l > 0) {
                    RenderSystem.pushMatrix();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    int var10001 = j - 2;
                    int var10002 = k - 2;
                    int var10003 = j + i + 2;
                    this.getFontRenderer().getClass();
                    DrawableHelper.fill(matrices, var10001, var10002, var10003, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
                    this.getFontRenderer().drawWithShadow(matrices, tooltip, (float) j, (float) k, 16777215 + (l << 24));
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
            }
        }

        this.client.getProfiler().pop();
        ci.cancel();
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
    private void renderMountHealth(MatrixStack matrices, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"), cancellable = true)
    private void renderMountJumpBar(MatrixStack matrices, int x, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderExperienceBar(MatrixStack matrices, int x, CallbackInfo ci) {
        if (CEUtil.cannotRenderHotbars(this.client)) {
            ci.cancel();
        }
    }
}
