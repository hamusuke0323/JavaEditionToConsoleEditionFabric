package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.invoker.MinecraftServerInvoker;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UserCache;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
@Environment(EnvType.CLIENT)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow
    private boolean paused;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private MinecraftClient client;

    private IntegratedServerMixin(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(thread, impl, session, saveProperties, resourcePackManager, proxy, dataFixer, serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
        Profiler profiler = this.getProfiler();

        if (!this.paused) {
            super.tick(shouldKeepTicking);
            int i = Math.max(2, this.client.options.viewDistance + -1);
            if (i != this.getPlayerManager().getViewDistance()) {
                LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerManager().getViewDistance());
                this.getPlayerManager().setViewDistance(i);
            }

            int j = JECEClient.jeceOptions.autoSaveTicks;
            if (j >= 900) {
                int k = this.getTicks() % j;
                int l = j - k;
                if (l <= 100 && l % 20 == 0) {
                    this.getPlayerManager().sendToAll(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new TranslatableText("gui.autosave.start", l / 20)));
                }

                if (k == 0) {
                    LOGGER.info("Autosave started");
                    profiler.push("autoSave");
                    ((MinecraftServerInvoker) this).saveAll();
                    profiler.pop();
                    LOGGER.info("Autosave finished");
                }
            }
        }

        ci.cancel();
    }
}
