package com.hamusuke.jece.mixin;

import com.hamusuke.jece.invoker.MinecraftServerInvoker;
import com.hamusuke.jece.invoker.ServerWorldInvoker;
import com.hamusuke.jece.network.NetworkManager;
import com.hamusuke.jece.server.JECEServer;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.MetricsData;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.snooper.Snooper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerInvoker {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    protected SaveProperties saveProperties;

    @Shadow
    @Final
    protected LevelStorage.Session session;

    @Shadow
    @Final
    protected DynamicRegistryManager.Impl registryManager;

    @Shadow
    private int ticks;

    @Shadow
    protected abstract void tickWorlds(BooleanSupplier shouldKeepTicking);

    @Shadow
    private long lastPlayerSampleUpdate;

    @Shadow
    @Final
    private ServerMetadata metadata;

    @Shadow
    @Final
    private Random random;

    @Shadow
    private PlayerManager playerManager;

    @Shadow
    private Profiler profiler;

    @Shadow
    @Final
    private Snooper snooper;

    @Shadow
    @Final
    public long[] lastTickLengths;

    @Shadow
    private float tickTime;

    @Shadow
    @Final
    private MetricsData metricsData;

    @Shadow
    public abstract int getMaxPlayerCount();

    @Shadow
    public abstract int getCurrentPlayerCount();

    @Shadow
    public abstract Iterable<ServerWorld> getWorlds();

    @Shadow
    public abstract ServerWorld getOverworld();

    @Shadow
    @Final
    private BossBarManager bossBarManager;

    @Shadow
    public abstract boolean isDedicated();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (this.isDedicated()) {
            long l = Util.getMeasuringTimeNano();
            ++this.ticks;
            this.tickWorlds(shouldKeepTicking);
            if (l - this.lastPlayerSampleUpdate >= 5000000000L) {
                this.lastPlayerSampleUpdate = l;
                this.metadata.setPlayers(new ServerMetadata.Players(this.getMaxPlayerCount(), this.getCurrentPlayerCount()));
                GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
                int i = MathHelper.nextInt(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

                for (int j = 0; j < gameProfiles.length; ++j) {
                    gameProfiles[j] = this.playerManager.getPlayerList().get(i + j).getGameProfile();
                }

                Collections.shuffle(Arrays.asList(gameProfiles));
                this.metadata.getPlayers().setSample(gameProfiles);
            }

            int a = JECEServer.jeceServerOptions.autoSaveTicks;
            if (a >= 900) {
                int b = this.ticks % a;
                int c = a - b;
                if (c <= 100 && c % 20 == 0) {
                    this.playerManager.sendToAll(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, new TranslatableText("gui.autosave.start", c / 20)));
                }

                if (b == 0) {
                    LOGGER.info("Autosave started");
                    this.profiler.push("autoSave");
                    this.saveAll();
                    this.profiler.pop();
                    LOGGER.info("Autosave finished");
                }
            }

            this.profiler.push("snooper");
            if (!this.snooper.isActive() && this.ticks > 100) {
                this.snooper.method_5482();
            }

            if (this.ticks % 6000 == 0) {
                this.snooper.update();
            }

            this.profiler.pop();
            this.profiler.push("tallying");
            long m = this.lastTickLengths[this.ticks % 100] = Util.getMeasuringTimeNano() - l;
            this.tickTime = this.tickTime * 0.8F + (float) m / 1000000.0F * 0.19999999F;
            long n = Util.getMeasuringTimeNano();
            this.metricsData.pushSample(n - l);
            this.profiler.pop();
            ci.cancel();
        }
    }

    private void sendToAll(Text text, float progress) {
        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeText(text);
        packetByteBuf.writeFloat(progress);
        this.playerManager.sendToAll(ServerPlayNetworking.createS2CPacket(NetworkManager.AUTO_SAVE_PACKET_ID, packetByteBuf));
    }

    public void saveAll() {
        Text savePlayers = new TranslatableText("menu.save.players");
        Text savingLevel = new TranslatableText("menu.savingLevel");
        Text savingChunks = new TranslatableText("menu.savingChunks");
        this.sendToAll(savePlayers, 0.0F);
        this.sleep(new Random().nextInt(50) + 500L);

        PlayerManager list = this.playerManager;
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
        this.saveProperties.setCustomBossEvents(this.bossBarManager.toTag());
        this.session.backupLevelDataFile(this.registryManager, this.saveProperties, this.playerManager.getUserData());

        this.sendToAll(new TranslatableText("menu.savelevel.finally"), 0.0F);
        this.sleep(1500L);
        this.playerManager.sendToAll(ServerPlayNetworking.createS2CPacket(NetworkManager.AUTO_SAVE_END_PACKET_ID, PacketByteBufs.create()));
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}
