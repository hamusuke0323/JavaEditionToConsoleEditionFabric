package com.hamusuke.jece.mixin.client;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.net.Proxy;

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

    /*
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    protected void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        boolean bl = this.paused;
        this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();

        Profiler profiler = this.getProfiler();

        if (!bl && this.paused) {
            profiler.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            this.getPlayerManager().saveAllPlayerData();
            this.save(false, false, false);
            profiler.pop();
        }


        if (!this.paused) {
            super.tick(shouldKeepTicking);
            int i = Math.max(2, this.client.options.viewDistance + -1);
            if (i != this.getPlayerManager().getViewDistance()) {
                LOGGER.info("Changing view distance to {}, from {}", i, this.getPlayerManager().getViewDistance());
                this.getPlayerManager().setViewDistance(i);
            }

            LOGGER.info("Autosave started");
            profiler.push("autoSave");
            PacketByteBuf packetByteBuf = PacketByteBufs.create();

            this.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(NetworkManager.AUTO_SAVE_PACKET_ID, packetByteBuf));
        }

        ci.cancel();
    }
    */
}
