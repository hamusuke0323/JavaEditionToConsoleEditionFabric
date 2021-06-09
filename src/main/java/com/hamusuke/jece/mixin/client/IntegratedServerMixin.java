package com.hamusuke.jece.mixin.client;

import com.hamusuke.jece.client.MainClient;
import com.hamusuke.jece.client.invoker.IntegratedServerInvoker;
import com.hamusuke.jece.invoker.ServerWorldInvoker;
import com.hamusuke.jece.network.NetworkManager;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UserCache;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.Random;
import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
@Environment(EnvType.CLIENT)
public abstract class IntegratedServerMixin extends MinecraftServer implements IntegratedServerInvoker {
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

            if (MainClient.jeceOptions.autoSaveTicks < 900) {
                int j = MainClient.jeceOptions.autoSaveTicks;
                int k = this.getTicks() % j;
                int l = j - k;
                if (l <= 100 && l % 20 == 0) {
                    this.getPlayerManager().sendToAll(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new TranslatableText("gui.autosave.start", l / 20)));
                }

                if (k == 0) {
                    LOGGER.info("Autosave started");
                    profiler.push("autoSave");
                    this.saveAll();
                    profiler.pop();
                    LOGGER.info("Autosave finished");
                }
            }
        }

        ci.cancel();
    }

    private void sendToAll(Text text, float progress) {
        this.sendToAll(text, progress, true);
    }

    private void sendToAll(Text text, float progress, boolean keepScreen) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeText(text);
        packetByteBuf.writeFloat(progress);
        packetByteBuf.writeBoolean(keepScreen);
        this.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(NetworkManager.AUTO_SAVE_PACKET_ID, packetByteBuf));
    }

    public void saveAll() {
        Text savePlayers = new TranslatableText("menu.save.players");
        Text savingLevel = new TranslatableText("menu.savingLevel");
        Text savingChunks = new TranslatableText("menu.savingChunks");
        this.sendToAll(savePlayers, 0.0F);
        this.sleep(new Random().nextInt(50) + 500L);

        PlayerManager list = this.getPlayerManager();
        if (list != null) {
            this.sendToAll(savePlayers, 0.1F);
            LOGGER.info("Saving players");
            list.saveAllPlayerData();
            this.sendToAll(savePlayers, 0.2F);
        }

        this.sendToAll(savePlayers, 0.3F);
        this.sleep(new Random().nextInt(50) + 500L);

        LOGGER.info("Saving worlds");
        for (ServerWorld serverWorld : this.getWorlds()) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverWorld, serverWorld.getRegistryKey().getValue());

            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            if (!serverWorld.savingDisabled) {
                this.sendToAll(savePlayers, 0.5F);
                this.sendToAll(savingLevel, 0.7F);
                this.sleep(new Random().nextInt(50) + 500L);
                ((ServerWorldInvoker) serverWorld).saveLevel();
                this.sendToAll(savingLevel, 0.8F);
                this.sendToAll(savingChunks, 0.9F);
                serverChunkManager.save(true);
            }
        }

        ServerWorld serverWorld2 = this.getOverworld();
        ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
        serverWorldProperties.setWorldBorder(serverWorld2.getWorldBorder().write());
        this.saveProperties.setCustomBossEvents(this.getBossBarManager().toTag());
        this.session.backupLevelDataFile(this.registryManager, this.saveProperties, this.getPlayerManager().getUserData());

        this.sendToAll(new TranslatableText("menu.savelevel.finally"), 0.0F);
        this.sleep(1500L);
        this.getPlayerManager().sendToAll(ServerPlayNetworking.createS2CPacket(NetworkManager.AUTO_SAVE_END_PACKET_ID, PacketByteBufs.create()));
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
