package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    private int renderPlayerTicks;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (this.client.player != null && this.client.currentScreen == null && JECEClient.jeceOptions.displayPlayerOnScreen && (this.client.player.abilities.flying || this.client.player.isSprinting() || this.client.player.isFallFlying() || this.client.player.isSneaking())) {
            this.renderPlayerTicks = 7;
        } else if (this.renderPlayerTicks != 0) {
            this.renderPlayerTicks--;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (JECEClient.jeceOptions.displayPlayerOnScreen && this.client.player != null && !this.client.skipGameRender && tick && this.client.world != null && !this.client.options.hudHidden && this.client.currentScreen == null && this.renderPlayerTicks != 0) {
            this.drawPlayerOnLeft(20, 50, 15, 150.0F, -21.0F, this.client.player.isFallFlying());
        }
    }

    private void drawPlayerOnLeft(int posX, int posY, int scale, float yaw, float pitch, boolean isElytraFlying) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternion = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion1 = Vector3f.POSITIVE_X.getDegreesQuaternion(pitch);
        quaternion.hamiltonProduct(quaternion1);
        matrixstack.multiply(quaternion);
        float f = this.client.player.bodyYaw;
        float f1 = this.client.player.yaw;
        float f2 = this.client.player.prevHeadYaw;
        float f3 = this.client.player.headYaw;
        this.client.player.bodyYaw = yaw;
        this.client.player.yaw = isElytraFlying ? this.client.player.yaw : yaw;
        this.client.player.prevHeadYaw = isElytraFlying ? yaw : this.client.player.yaw;
        this.client.player.headYaw = isElytraFlying ? yaw : this.client.player.yaw;
        EntityRenderDispatcher entityRendererManager = this.client.getEntityRenderDispatcher();
        quaternion1.conjugate();
        entityRendererManager.setRotation(quaternion1);
        entityRendererManager.setRenderShadows(false);
        VertexConsumerProvider.Immediate vertexConsumerProvider$Immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        entityRendererManager.render(this.client.player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, vertexConsumerProvider$Immediate, 15728880);
        vertexConsumerProvider$Immediate.draw();
        entityRendererManager.setRenderShadows(true);
        this.client.player.bodyYaw = f;
        this.client.player.yaw = f1;
        this.client.player.prevHeadYaw = f2;
        this.client.player.headYaw = f3;
        RenderSystem.popMatrix();
    }
}
